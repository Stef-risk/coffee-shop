# Solidity 智能合约入门项目

> 面向 SpringBoot 后端开发者的区块链智能合约学习指南

---

## 🗺️ 学习路线图

```
第一阶段：基础概念（本项目）
  ├── Solidity 语法对比 Java
  ├── 简单存储合约
  ├── ERC-20 代币合约
  └── NFT (ERC-721) 合约

第二阶段：进阶
  ├── DeFi 协议
  ├── 合约安全
  └── Gas 优化
```

---

## 🔄 Java/SpringBoot vs Solidity 对比

| Java/SpringBoot | Solidity | 说明 |
|---|---|---|
| `class` | `contract` | 定义主体 |
| `interface` | `interface` | 接口定义 |
| `extends` | `is` | 继承 |
| `public/private` | `public/private/internal/external` | 访问控制 |
| `constructor` | `constructor` | 构造函数 |
| `static` 变量 | 状态变量 | 持久化存储到区块链 |
| `@Transactional` | 每个函数调用都是原子的 | 事务 |
| `try/catch` | `require/revert/assert` | 错误处理 |
| Maven/Gradle | npm/hardhat | 构建工具 |
| JUnit | Mocha/Chai + Hardhat | 测试框架 |
| Spring DI | 无内置DI，用库地址引用 | 依赖 |

---

## 📁 项目结构

```
smart-contract-starter/
├── contracts/                  # 合约源码（类比 src/main/java）
│   ├── 01_SimpleStorage.sol    # 入门：简单存储
│   ├── 02_CoffeeToken.sol      # ERC-20 代币
│   └── 03_CoffeeNFT.sol        # ERC-721 NFT
├── test/                       # 测试（类比 src/test/java）
│   ├── SimpleStorage.test.js
│   ├── CoffeeToken.test.js
│   └── CoffeeNFT.test.js
├── scripts/                    # 部署脚本（类比 Flyway 迁移脚本）
│   ├── deploy-simple-storage.js
│   ├── deploy-coffee-token.js
│   └── deploy-coffee-nft.js
├── hardhat.config.js           # 类比 application.yml
└── package.json
```

---

## 🚀 快速开始

### 1. 安装依赖

```bash
# 进入项目目录
cd smart-contract-starter

# 安装依赖（类比 mvn install）
npm install
```

### 2. 编译合约

```bash
# 编译所有合约（类比 mvn compile）
npx hardhat compile
```

### 3. 运行测试

```bash
# 运行所有测试（类比 mvn test）
npx hardhat test

# 查看 Gas 费用报告
REPORT_GAS=true npx hardhat test
```

### 4. 本地部署

```bash
# 启动本地区块链节点（类比启动本地数据库）
npx hardhat node

# 新终端：部署合约到本地节点
npx hardhat run scripts/deploy-simple-storage.js --network localhost
npx hardhat run scripts/deploy-coffee-token.js --network localhost
npx hardhat run scripts/deploy-coffee-nft.js --network localhost
```

### 5. 部署到测试网（Sepolia）

```bash
# 配置 .env 文件（见下方说明）
npx hardhat run scripts/deploy-coffee-token.js --network sepolia
```

---

## ⚙️ 环境配置

创建 `.env` 文件（**不要提交到 git**）：

```env
# MetaMask 钱包私钥（测试账户，不要用真实资产账户！）
PRIVATE_KEY=your_metamask_private_key_here

# Alchemy 或 Infura 的 RPC URL
SEPOLIA_RPC_URL=https://eth-sepolia.g.alchemy.com/v2/your_api_key

# Etherscan API Key（用于验证合约源码）
ETHERSCAN_API_KEY=your_etherscan_api_key
```

---

## 📚 核心概念解释

### 区块链 vs 传统数据库

```
传统后端（你熟悉的）:
  请求 → Controller → Service → Repository → MySQL
                                                ↑ 可修改、可删除

区块链:
  请求 → 钱包签名 → 广播交易 → 矿工打包 → 区块链
                                              ↑ 不可篡改、永久记录
```

### Gas 费用
- 类比：每次数据库写操作都需要支付手续费
- 读操作（view/pure）：免费
- 写操作：消耗 Gas（以 ETH 支付）

### 合约地址
- 类比：你的 Spring 应用部署后有一个 URL，合约部署后有一个地址
- 地址是唯一的，部署后不可更改（除非使用可升级合约模式）

---

## 🛠️ 推荐工具

| 工具 | 用途 | 类比 |
|---|---|---|
| [Remix IDE](https://remix.ethereum.org) | 在线合约编辑器 | IntelliJ IDEA |
| [MetaMask](https://metamask.io) | 浏览器钱包 | 数据库客户端 |
| [Hardhat](https://hardhat.org) | 开发框架 | Spring Boot |
| [OpenZeppelin](https://openzeppelin.com) | 合约库 | Spring Security |
| [Etherscan](https://etherscan.io) | 区块链浏览器 | 数据库管理工具 |
| [Alchemy](https://alchemy.com) | 节点服务 | 云数据库服务 |
