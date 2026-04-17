const { expect } = require("chai");
const { ethers } = require("hardhat");
const { parseEther, formatEther } = ethers;

describe("CoffeeToken (ERC-20)", function () {

  let coffeeToken;
  let owner;
  let buyer;
  let addr2;

  const TOKENS_PER_ETH = 100n;

  beforeEach(async function () {
    [owner, buyer, addr2] = await ethers.getSigners();

    const CoffeeToken = await ethers.getContractFactory("CoffeeToken");
    coffeeToken = await CoffeeToken.deploy(owner.address);
  });

  describe("ERC20 基本属性", function () {
    it("名称应为 CoffeeToken", async function () {
      expect(await coffeeToken.name()).to.equal("CoffeeToken");
    });

    it("符号应为 CAFE", async function () {
      expect(await coffeeToken.symbol()).to.equal("CAFE");
    });

    it("第一个账户应有初始供应量", async function () {
      const balance = await coffeeToken.balanceOf(owner.address);
      expect(balance).to.equal(parseEther("10000"));
    });
  });

  describe("buyTokens", function () {
    it("发逃1 ETH 应得到 100 CAFE", async function () {
      const ethAmount = parseEther("1");
      await coffeeToken.connect(buyer).buyTokens({ value: ethAmount });

      const balance = await coffeeToken.balanceOf(buyer.address);
      expect(balance).to.equal(parseEther("100"));
    });

    it("应发射 TokensPurchased 事件", async function () {
      const ethAmount = parseEther("0.5");
      await expect(
        coffeeToken.connect(buyer).buyTokens({ value: ethAmount })
      ).to.emit(coffeeToken, "TokensPurchased")
        .withArgs(buyer.address, ethAmount, parseEther("50"));
    });

    it("不发送ETH应失败", async function () {
      await expect(
        coffeeToken.connect(buyer).buyTokens({ value: 0 })
      ).to.be.revertedWith("CoffeeToken: send ETH to buy tokens");
    });
  });

  describe("redeemTokens", function () {
    beforeEach(async function () {
      // 先购买一些 Token
      await coffeeToken.connect(buyer).buyTokens({ value: parseEther("2") });
    });

    it("应能兑换 Token 回 ETH", async function () {
      const tokenAmount = parseEther("100"); // 100 CAFE = 1 ETH
      const buyerBefore = await ethers.provider.getBalance(buyer.address);

      await coffeeToken.connect(buyer).redeemTokens(tokenAmount);

      const buyerAfter = await ethers.provider.getBalance(buyer.address);
      expect(buyerAfter).to.be.gt(buyerBefore - parseEther("0.01")); // Gas减少应小于0.01
    });

    it("余额不足时应失败", async function () {
      await expect(
        coffeeToken.connect(buyer).redeemTokens(parseEther("99999"))
      ).to.be.revertedWith("CoffeeToken: insufficient balance");
    });
  });

  describe("mint (onlyOwner)", function () {
    it("Owner 应能铸造新Token", async function () {
      await coffeeToken.mint(addr2.address, parseEther("500"));
      expect(await coffeeToken.balanceOf(addr2.address)).to.equal(parseEther("500"));
    });

    it("非 Owner 不能铸造", async function () {
      await expect(
        coffeeToken.connect(buyer).mint(buyer.address, parseEther("100"))
      ).to.be.revertedWithCustomError(coffeeToken, "OwnableUnauthorizedAccount");
    });
  });

  describe("ERC20 transfer", function () {
    it("应能转账 Token", async function () {
      await coffeeToken.transfer(buyer.address, parseEther("100"));
      expect(await coffeeToken.balanceOf(buyer.address)).to.equal(parseEther("100"));
    });

    it("应能 approve + transferFrom", async function () {
      await coffeeToken.approve(addr2.address, parseEther("50"));
      await coffeeToken.connect(addr2).transferFrom(owner.address, buyer.address, parseEther("50"));
      expect(await coffeeToken.balanceOf(buyer.address)).to.equal(parseEther("50"));
    });
  });
});
