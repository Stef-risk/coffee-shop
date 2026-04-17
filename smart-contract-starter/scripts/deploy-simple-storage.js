/**
 * SimpleStorage 部署脚本
 * 类比： Flyway 数据库迁移脚本
 *
 * 运行：
 *   npx hardhat run scripts/deploy-simple-storage.js --network localhost
 */
const { ethers } = require("hardhat");

async function main() {
  console.log("\n🚀 开始部署 SimpleStorage...");

  // 获取部署者账户（类比数据库连接用户）
  const [deployer] = await ethers.getSigners();
  console.log("💰 部署者地址:", deployer.address);

  const balance = await ethers.provider.getBalance(deployer.address);
  console.log("💰 账户余额:", ethers.formatEther(balance), "ETH");

  // 获取合约工厂（类比 Spring 的 BeanFactory）
  const SimpleStorage = await ethers.getContractFactory("SimpleStorage");

  // 部署合约（类比创建数据库表）
  console.log("\n⚙️  正在部署合约...");
  const contract = await SimpleStorage.deploy("MyFirstStorage");
  await contract.waitForDeployment();

  const address = await contract.getAddress();
  console.log("\n✅ SimpleStorage 部署成功!");
  console.log("📍 合约地址:", address);

  // 验证部署
  const contractName = await contract.contractName();
  const owner = await contract.owner();
  console.log("\n📋 验证部署结果:");
  console.log("  合约名称:", contractName);
  console.log("  Owner地址:", owner);
  console.log("  当前存储数字:", (await contract.storedNumber()).toString());

  // 测试一下存储功能
  console.log("\n🧪 测试存储操作...");
  const tx = await contract.storeNumber(42);
  await tx.wait();
  console.log("  存储数字 42, 交易Hash:", tx.hash);
  console.log("  当前存储数字:", (await contract.storedNumber()).toString());

  console.log("\n🎉 所有操作成功!");
  console.log("ℹ️  如果在测试网上部署，可以在 Etherscan 查看:");
  console.log(`   https://sepolia.etherscan.io/address/${address}`);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("❌ 部署失败:", error);
    process.exit(1);
  });
