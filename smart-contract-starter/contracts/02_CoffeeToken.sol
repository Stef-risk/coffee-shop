// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Burnable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Permit.sol";

/**
 * @title CoffeeToken
 * @dev ERC-20同质化代币——和星巴克积分类似的和和敌係数
 *
 * 类比关系：
 *   ERC20      ≈  JPA BaseEntity（包含ERC20标准接口）
 *   Ownable    ≈  Spring Security的管理员权限
 *   Burnable   ≈  软删除功能
 *   Permit     ≈  JWT无模签名授权
 *
 * 功能：
 *   - 购买咊唏可以获得 CoffeeToken
 *   - Token 可以兑换底层 ETH
 *   - 仅 Owner 可以铸造新 Token
 */
contract CoffeeToken is ERC20, ERC20Burnable, Ownable, ERC20Permit {

    // 价格：1 ETH = 100 CoffeeToken
    uint256 public constant TOKENS_PER_ETH = 100;
    uint256 public constant MAX_SUPPLY = 1_000_000 * 10 ** 18; // 100万枚上限

    // 事件
    event TokensPurchased(address indexed buyer, uint256 ethAmount, uint256 tokenAmount);
    event TokensRedeemed(address indexed redeemer, uint256 tokenAmount, uint256 ethAmount);

    /**
     * @dev 构造函数
     * @param initialOwner 初始拥有者（平时传入部署者地址）
     */
    constructor(address initialOwner)
        ERC20("CoffeeToken", "CAFE")       // 代币名称和符号（类比 USDT, ETH）
        Ownable(initialOwner)
        ERC20Permit("CoffeeToken")
    {
        // 初始化时给部署者铸造 10000 枚
        _mint(initialOwner, 10_000 * 10 ** decimals());
    }

    /**
     * @dev 购买Token：发送ETH得到CoffeeToken
     *      payable = 这个函数可以接收ETH
     *
     * 类比：用户充値 → 调用支付接口 → 返回积分
     */
    function buyTokens() public payable {
        require(msg.value > 0, "CoffeeToken: send ETH to buy tokens");

        uint256 tokenAmount = msg.value * TOKENS_PER_ETH;
        require(totalSupply() + tokenAmount <= MAX_SUPPLY, "CoffeeToken: exceeds max supply");

        _mint(msg.sender, tokenAmount);
        emit TokensPurchased(msg.sender, msg.value, tokenAmount);
    }

    /**
     * @dev 兑换Token回 ETH
     * @param tokenAmount 要兑换的Token数量
     */
    function redeemTokens(uint256 tokenAmount) public {
        require(tokenAmount > 0, "CoffeeToken: amount must be > 0");
        require(balanceOf(msg.sender) >= tokenAmount, "CoffeeToken: insufficient balance");

        uint256 ethAmount = tokenAmount / TOKENS_PER_ETH;
        require(address(this).balance >= ethAmount, "CoffeeToken: insufficient ETH in contract");

        // 先销毁Token，再转账ETH（防止重入攻击！）
        _burn(msg.sender, tokenAmount);
        payable(msg.sender).transfer(ethAmount);

        emit TokensRedeemed(msg.sender, tokenAmount, ethAmount);
    }

    /**
     * @dev Owner手动铸造（类比管理员治理功能）
     */
    function mint(address to, uint256 amount) public onlyOwner {
        require(totalSupply() + amount <= MAX_SUPPLY, "CoffeeToken: exceeds max supply");
        _mint(to, amount);
    }

    /**
     * @dev 查询合约持有的ETH余额
     */
    function getContractBalance() public view returns (uint256) {
        return address(this).balance;
    }

    /**
     * @dev Owner提取合约中的ETH
     */
    function withdrawETH() public onlyOwner {
        uint256 balance = address(this).balance;
        require(balance > 0, "CoffeeToken: no ETH to withdraw");
        payable(owner()).transfer(balance);
    }

    /**
     * @dev 接收直接转入的ETH（receive和fallback是Solidity特有）
     *      类比： Spring MVC 默认处理器
     */
    receive() external payable {
        if (msg.value > 0) {
            uint256 tokenAmount = msg.value * TOKENS_PER_ETH;
            if (totalSupply() + tokenAmount <= MAX_SUPPLY) {
                _mint(msg.sender, tokenAmount);
                emit TokensPurchased(msg.sender, msg.value, tokenAmount);
            }
        }
    }
}
