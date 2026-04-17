/**
 * CoffeeToken (ERC-20) 部署脚本
 *
 * 运行：
 *   npx hardhat run scripts/deploy-coffee-token.js --network localhost
 *   npx hardhat run scripts/deploy-coffee-token.js --network sepolia
 */
const { ethers, run, network } = require("hardhat");

async function main() {
  console.log("\n🚀 开始部署 CoffeeToken (ERC-20)...");
  console.log("🌐 目标网络:", network.name);

  const [deployer] = await ethers.getSigners();
  console.log("💰 部署者地址:", deployer.address);

  const balance = await ethers.provider.getBalance(deployer.address);
  console.log("💰 账户余额:", ethers.formatEther(balance), "ETH");

  const CoffeeToken = await ethers.getContractFactory("CoffeeToken");

  console.log("\n⚙️  正在部署...");
  const token = await CoffeeToken.deploy(deployer.address);
  await token.waitForDeployment();

  const address = await token.getAddress();
  console.log("\n✅ CoffeeToken 部署成功!");
  console.log("📍 合约地址:", address);

  // 验证
  console.log("\n📋 验证 Token 信息:");
  console.log("  名称:", await token.name());
  console.log("  符号:", await token.symbol());
  console.log("  总供应量:", ethers.formatEther(await token.totalSupply()), "CAFE");
  console.log("  Owner余额:", ethers.formatEther(await token.balanceOf(deployer.address)), "CAFE");
  console.log("  1 ETH 可购买:", (await token.TOKENS_PER_ETH()).toString(), "CAFE");

  // 测试购买（只在本地网络测试）
  if (network.name === "localhost" || network.name === "hardhat") {
    console.log("\n🧪 本地测试：购买 Token...");
    const buyTx = await token.buyTokens({ value: ethers.parseEther("1") });
    await buyTx.wait();
    console.log("  购买成功！当前余额:", ethers.formatEther(await token.balanceOf(deployer.address)), "CAFE");
  }

  // 在 Sepolia 上验证源码（测试网上需等待几个区块确认）
  if (network.name === "sepolia") {
    console.log("\n⏳ 等待6个区块确认再验证源码...");
    await new Promise(resolve => setTimeout(resolve, 60000));

    try {
      await run("verify:verify", {
        address: address,
        constructorArguments: [deployer.address],
      });
      console.log("✅ 源码验证成功!");
    } catch (e) {
      console.log("⚠️  验证失败:", e.message);
    }
  }

  console.log("\n🎉 部署完成!");
  if (network.name === "sepolia") {
    console.log(`🔗 Etherscan: https://sepolia.etherscan.io/address/${address}`);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("❌ 部署失败:", error);
    process.exit(1);
  });
