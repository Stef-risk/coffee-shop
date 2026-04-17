/**
 * CoffeeNFT (ERC-721) 部署脚本
 *
 * 运行：
 *   npx hardhat run scripts/deploy-coffee-nft.js --network localhost
 *   npx hardhat run scripts/deploy-coffee-nft.js --network sepolia
 */
const { ethers, run, network } = require("hardhat");

async function main() {
  console.log("\n🚀 开始部署 CoffeeNFT (ERC-721)...");
  console.log("🌐 目标网络:", network.name);

  const [deployer] = await ethers.getSigners();
  console.log("💰 部署者地址:", deployer.address);

  const CoffeeNFT = await ethers.getContractFactory("CoffeeNFT");

  console.log("\n⚙️  正在部署...");
  const nft = await CoffeeNFT.deploy(deployer.address);
  await nft.waitForDeployment();

  const address = await nft.getAddress();
  console.log("\n✅ CoffeeNFT 部署成功!");
  console.log("📍 合约地址:", address);

  console.log("\n📋 验证 NFT 信息:");
  console.log("  名称:", await nft.name());
  console.log("  符号:", await nft.symbol());
  console.log("  铸造价格:", ethers.formatEther(await nft.MINT_PRICE()), "ETH");
  console.log("  最大供应量:", (await nft.MAX_SUPPLY()).toString());

  // 本地测试铸造
  if (network.name === "localhost" || network.name === "hardhat") {
    console.log("\n🧪 本地测试：铸造 NFT...");
    const mintTx = await nft.mintCoffeeCard(
      "ipfs://QmExample/0.json",
      { value: ethers.parseEther("0.01") }
    );
    await mintTx.wait();

    const tokenId = 0;
    const card = await nft.coffeeCards(tokenId);
    console.log("✅ 铸造成功!");
    console.log("  TokenId:", tokenId);
    console.log("  咊唏种类:", card.coffeeType);
    console.log("  等级:", card.level.toString());
    console.log("  拥有者:", await nft.ownerOf(tokenId));
  }

  // Sepolia 验证源码
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
    console.log("🎨 OpenSea Testnet: https://testnets.opensea.io/");
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("❌ 部署失败:", error);
    process.exit(1);
  });
