// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

/**
 * @title CoffeeNFT
 * @dev ERC-721 NFT合约——咊唏会员卡
 *
 * 类比关系：
 *   ERC721            ≈  JPA 实体基类
 *   ERC721URIStorage  ≈  文件存储扩展（存储图片元数据指针）
 *   ERC721Enumerable  ≈  分页查询支持
 *   Counters.Counter  ≈  数据库自增主键
 *
 * 功能：
 *   - 每个咊唏铸造一张独一无二的会员卡NFT
 *   - NFT可自由转让
 *   - 支持查询某地址拥有的所有NFT
 */
contract CoffeeNFT is ERC721, ERC721URIStorage, ERC721Enumerable, Ownable {
    using Counters for Counters.Counter;

    Counters.Counter private _tokenIdCounter;  // 自增 ID（类比数据库自增主键）

    uint256 public constant MINT_PRICE = 0.01 ether;  // 铸造价格
    uint256 public constant MAX_SUPPLY = 10_000;      // 最大发行量

    // NFT 属性（元数据）
    struct CoffeeCard {
        string coffeeType;    // 咊唏种类
        uint256 mintedAt;     // 铸造时间（类比创建时间）
        uint256 level;        // 会员等级
    }

    mapping(uint256 => CoffeeCard) public coffeeCards;   // tokenId => 属性
    mapping(address => bool) public hasMinted;           // 防止一人多张

    string[] private coffeeTypes = [
        "Espresso", "Americano", "Latte", "Cappuccino",
        "Mocha", "FlatWhite", "Macchiato", "ColdBrew"
    ];

    // 事件
    event CoffeeMinted(address indexed to, uint256 indexed tokenId, string coffeeType);
    event LevelUpgraded(uint256 indexed tokenId, uint256 newLevel);

    constructor(address initialOwner)
        ERC721("CoffeeNFT", "CNFT")
        Ownable(initialOwner)
    {}

    /**
     * @dev 铸造NFT（每个地址只能一张）
     *      payable: 需要支付 0.01 ETH
     */
    function mintCoffeeCard(string memory tokenURI) public payable {
        require(!hasMinted[msg.sender], "CoffeeNFT: already minted");
        require(msg.value >= MINT_PRICE, "CoffeeNFT: insufficient payment");
        require(totalSupply() < MAX_SUPPLY, "CoffeeNFT: max supply reached");

        uint256 tokenId = _tokenIdCounter.current();
        _tokenIdCounter.increment();

        // 外嘖取咊唏种类（只是示例，真实项目用Chainlink VRF）
        string memory coffeeType = coffeeTypes[tokenId % coffeeTypes.length];

        coffeeCards[tokenId] = CoffeeCard({
            coffeeType: coffeeType,
            mintedAt: block.timestamp,
            level: 1
        });

        hasMinted[msg.sender] = true;
        _safeMint(msg.sender, tokenId);    // 铸造NFT到调用者地址
        _setTokenURI(tokenId, tokenURI);   // 设置元数据 URI（指向IPFS）

        emit CoffeeMinted(msg.sender, tokenId, coffeeType);

        // 多仙8的费用退回
        if (msg.value > MINT_PRICE) {
            payable(msg.sender).transfer(msg.value - MINT_PRICE);
        }
    }

    /**
     * @dev 升级会员卡（仅Owner）
     */
    function upgradeCard(uint256 tokenId) public onlyOwner {
        require(_ownerOf(tokenId) != address(0), "CoffeeNFT: token does not exist");
        coffeeCards[tokenId].level += 1;
        emit LevelUpgraded(tokenId, coffeeCards[tokenId].level);
    }

    /**
     * @dev 查询地址拥有的所有tokenId
     */
    function tokensOfOwner(address owner) public view returns (uint256[] memory) {
        uint256 tokenCount = balanceOf(owner);
        uint256[] memory tokenIds = new uint256[](tokenCount);
        for (uint256 i = 0; i < tokenCount; i++) {
            tokenIds[i] = tokenOfOwnerByIndex(owner, i);
        }
        return tokenIds;
    }

    /**
     * @dev Owner提取收益
     */
    function withdraw() public onlyOwner {
        uint256 balance = address(this).balance;
        require(balance > 0, "CoffeeNFT: nothing to withdraw");
        payable(owner()).transfer(balance);
    }

    // ==== 以下是必须重写的函数，因为多重继承 ====

    function _update(address to, uint256 tokenId, address auth)
        internal
        override(ERC721, ERC721Enumerable)
        returns (address)
    {
        return super._update(to, tokenId, auth);
    }

    function _increaseBalance(address account, uint128 value)
        internal
        override(ERC721, ERC721Enumerable)
    {
        super._increaseBalance(account, value);
    }

    function tokenURI(uint256 tokenId)
        public
        view
        override(ERC721, ERC721URIStorage)
        returns (string memory)
    {
        return super.tokenURI(tokenId);
    }

    function supportsInterface(bytes4 interfaceId)
        public
        view
        override(ERC721, ERC721Enumerable, ERC721URIStorage)
        returns (bool)
    {
        return super.supportsInterface(interfaceId);
    }
}
