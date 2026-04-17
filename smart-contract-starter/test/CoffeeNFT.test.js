const { expect } = require("chai");
const { ethers } = require("hardhat");
const { parseEther } = ethers;

describe("CoffeeNFT (ERC-721)", function () {

  let coffeeNFT;
  let owner;
  let minter1;
  let minter2;

  const MINT_PRICE = parseEther("0.01");
  const SAMPLE_URI = "ipfs://QmSampleHash123/metadata.json";

  beforeEach(async function () {
    [owner, minter1, minter2] = await ethers.getSigners();

    const CoffeeNFT = await ethers.getContractFactory("CoffeeNFT");
    coffeeNFT = await CoffeeNFT.deploy(owner.address);
  });

  describe("ERC721 基本属性", function () {
    it("名称应为 CoffeeNFT", async function () {
      expect(await coffeeNFT.name()).to.equal("CoffeeNFT");
    });

    it("符号应为 CNFT", async function () {
      expect(await coffeeNFT.symbol()).to.equal("CNFT");
    });

    it("初始总供应量应为 0", async function () {
      expect(await coffeeNFT.totalSupply()).to.equal(0);
    });
  });

  describe("mintCoffeeCard", function () {
    it("支付 0.01 ETH 应能铸造 NFT", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });

      expect(await coffeeNFT.totalSupply()).to.equal(1);
      expect(await coffeeNFT.balanceOf(minter1.address)).to.equal(1);
      expect(await coffeeNFT.ownerOf(0)).to.equal(minter1.address);
    });

    it("应设置正确的 tokenURI", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      expect(await coffeeNFT.tokenURI(0)).to.equal(SAMPLE_URI);
    });

    it("应设置咊唏属性", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      const card = await coffeeNFT.coffeeCards(0);

      expect(card.level).to.equal(1);
      expect(card.mintedAt).to.be.gt(0);
      expect(card.coffeeType).to.be.oneOf([
        "Espresso", "Americano", "Latte", "Cappuccino",
        "Mocha", "FlatWhite", "Macchiato", "ColdBrew"
      ]);
    });

    it("应发射 CoffeeMinted 事件", async function () {
      await expect(
        coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE })
      ).to.emit(coffeeNFT, "CoffeeMinted");
    });

    it("同一地址不能铸造两次", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      await expect(
        coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE })
      ).to.be.revertedWith("CoffeeNFT: already minted");
    });

    it("支付不足应失败", async function () {
      await expect(
        coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: parseEther("0.001") })
      ).to.be.revertedWith("CoffeeNFT: insufficient payment");
    });

    it("多付费用应达回", async function () {
      const overpay = parseEther("0.05");
      const balanceBefore = await ethers.provider.getBalance(minter1.address);
      const tx = await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: overpay });
      const receipt = await tx.wait();
      const gasCost = receipt.gasUsed * receipt.gasPrice;
      const balanceAfter = await ethers.provider.getBalance(minter1.address);

      // 应只扣除 0.01 ETH + Gas
      const expectedDeducted = MINT_PRICE + gasCost;
      expect(balanceBefore - balanceAfter).to.be.closeTo(expectedDeducted, parseEther("0.001"));
    });
  });

  describe("tokensOfOwner", function () {
    it("应返回地址拥有的所有tokenId", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });

      const tokens = await coffeeNFT.tokensOfOwner(minter1.address);
      expect(tokens.length).to.equal(1);
      expect(tokens[0]).to.equal(0);
    });
  });

  describe("NFT 转让 (transfer)", function () {
    it("应能转让 NFT 给其他地址", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      await coffeeNFT.connect(minter1).transferFrom(minter1.address, minter2.address, 0);

      expect(await coffeeNFT.ownerOf(0)).to.equal(minter2.address);
      expect(await coffeeNFT.balanceOf(minter1.address)).to.equal(0);
      expect(await coffeeNFT.balanceOf(minter2.address)).to.equal(1);
    });
  });

  describe("upgradeCard (onlyOwner)", function () {
    it("Owner 应能升级 NFT", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      await coffeeNFT.upgradeCard(0);

      const card = await coffeeNFT.coffeeCards(0);
      expect(card.level).to.equal(2);
    });

    it("非 Owner 不能升级", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });
      await expect(
        coffeeNFT.connect(minter1).upgradeCard(0)
      ).to.be.revertedWithCustomError(coffeeNFT, "OwnableUnauthorizedAccount");
    });
  });

  describe("withdraw", function () {
    it("Owner 应能提取收益", async function () {
      await coffeeNFT.connect(minter1).mintCoffeeCard(SAMPLE_URI, { value: MINT_PRICE });

      const ownerBefore = await ethers.provider.getBalance(owner.address);
      const tx = await coffeeNFT.withdraw();
      const receipt = await tx.wait();
      const gasCost = receipt.gasUsed * receipt.gasPrice;
      const ownerAfter = await ethers.provider.getBalance(owner.address);

      expect(ownerAfter + gasCost - ownerBefore).to.be.closeTo(MINT_PRICE, parseEther("0.0001"));
    });
  });
});
