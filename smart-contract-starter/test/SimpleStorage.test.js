const { expect } = require("chai");
const { ethers } = require("hardhat");

/**
 * SimpleStorage 合约测试
 *
 * 类比 JUnit + Mockito：
 *   describe  ≈  @TestClass
 *   it        ≈  @Test
 *   expect    ≈  Assertions.assertEquals
 *   before    ≈  @BeforeAll
 *   beforeEach ≈  @BeforeEach
 */
describe("SimpleStorage", function () {

  let simpleStorage;
  let owner;
  let addr1;
  let addr2;

  // 类比 @BeforeEach：每个测试前重新部署合约
  beforeEach(async function () {
    // 获取测试账户
    [owner, addr1, addr2] = await ethers.getSigners();

    // 部署合约（类比 new SimpleStorage()）
    const SimpleStorage = await ethers.getContractFactory("SimpleStorage");
    simpleStorage = await SimpleStorage.deploy("MyStorage");
  });

  // ==== 测试分组 ====

  describe("部署和初始化", function () {
    it("应该正确设置 owner", async function () {
      expect(await simpleStorage.owner()).to.equal(owner.address);
    });

    it("应该正确设置合约名称", async function () {
      expect(await simpleStorage.contractName()).to.equal("MyStorage");
    });

    it("初始 storedNumber 应为 0", async function () {
      expect(await simpleStorage.storedNumber()).to.equal(0);
    });
  });

  describe("storeNumber", function () {
    it("应该能存储数字", async function () {
      await simpleStorage.storeNumber(42);
      expect(await simpleStorage.getNumber()).to.equal(42);
    });

    it("应该发射 NumberUpdated 事件", async function () {
      // 类比 Mockito.verify(某事件被发射）
      await expect(simpleStorage.storeNumber(42))
        .to.emit(simpleStorage, "NumberUpdated")
        .withArgs(owner.address, 0, 42);
    });

    it("任意地址均可调用", async function () {
      await simpleStorage.connect(addr1).storeNumber(100);
      expect(await simpleStorage.getNumber()).to.equal(100);
    });
  });

  describe("addPerson", function () {
    it("应该能添加人员并更新映射", async function () {
      await simpleStorage.addPerson("Alice", 7);

      expect(await simpleStorage.getPeopleCount()).to.equal(1);
      expect(await simpleStorage.nameToNumber("Alice")).to.equal(7);
      expect(await simpleStorage.isRegistered(owner.address)).to.be.true;
    });

    it("应该正确存储结构体内容", async function () {
      await simpleStorage.connect(addr1).addPerson("Bob", 42);

      const person = await simpleStorage.people(0);
      expect(person.name).to.equal("Bob");
      expect(person.favoriteNumber).to.equal(42);
      expect(person.wallet).to.equal(addr1.address);
    });
  });

  describe("reset (onlyOwner)", function () {
    it("Owner 应能重置数字", async function () {
      await simpleStorage.storeNumber(999);
      await simpleStorage.reset();
      expect(await simpleStorage.getNumber()).to.equal(0);
    });

    it("非 Owner 调用应抛出错误", async function () {
      // 类比 @PreAuthorize 违规：期望抛出 AccessDeniedException
      await expect(
        simpleStorage.connect(addr1).reset()
      ).to.be.revertedWith("SimpleStorage: caller is not the owner");
    });
  });

  describe("add (pure 函数)", function () {
    it("应该返回两数之和", async function () {
      expect(await simpleStorage.add(3, 5)).to.equal(8);
      expect(await simpleStorage.add(0, 0)).to.equal(0);
    });
  });
});
