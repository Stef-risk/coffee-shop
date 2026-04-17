// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

/**
 * @title SimpleStorage
 * @dev 入门合约：基础存储操作
 *
 * 类比Java：
 *   contract SimpleStorage  ≈  public class SimpleStorage
 *   uint256 public number   ≈  private static int number（存到区块链上）
 *   view 函数           ≈  getter（免费调用）
 *   普通函数           ≈  setter（消耗Gas）
 */
contract SimpleStorage {

    // 状态变量：永久存储到区块链（类比数据库字段）
    uint256 public storedNumber;
    address public owner;        // 合约部署者地址
    string public contractName;

    // 结构体（类比Java的内部类/POJO）
    struct Person {
        string name;
        uint256 favoriteNumber;
        address wallet;
    }

    // 动态数组（类比 ArrayList）
    Person[] public people;

    // 映射（类比 HashMap）
    mapping(string => uint256) public nameToNumber;
    mapping(address => bool) public isRegistered;

    // 事件（类比 Spring ApplicationEvent，但是写到区块链日志）
    event NumberUpdated(address indexed updater, uint256 oldValue, uint256 newValue);
    event PersonAdded(address indexed wallet, string name, uint256 favoriteNumber);

    // 修饰器（类比Spring的 @PreAuthorize）
    modifier onlyOwner() {
        require(msg.sender == owner, "SimpleStorage: caller is not the owner");
        _;
    }

    /**
     * @dev 构造函数：合约部署时自动调用一次
     * 类比 @PostConstruct 或 ApplicationRunner
     */
    constructor(string memory _name) {
        owner = msg.sender;          // msg.sender = 当前调用者地址
        contractName = _name;
        storedNumber = 0;
    }

    /**
     * @dev 存储数字（嵌面操作，消耗Gas）
     * @param _number 要存储的数字
     */
    function storeNumber(uint256 _number) public {
        uint256 oldValue = storedNumber;
        storedNumber = _number;
        // 发射事件（类比发布Spring事件）
        emit NumberUpdated(msg.sender, oldValue, _number);
    }

    /**
     * @dev 读取数字（view＝只读，免费）
     * 类比 @Transactional(readOnly = true)
     */
    function getNumber() public view returns (uint256) {
        return storedNumber;
    }

    /**
     * @dev 添加人员信息
     */
    function addPerson(string memory _name, uint256 _favoriteNumber) public {
        // 创建结构体实例（类比 new Person()）
        Person memory newPerson = Person({
            name: _name,
            favoriteNumber: _favoriteNumber,
            wallet: msg.sender
        });

        people.push(newPerson);                         // 添加到数组
        nameToNumber[_name] = _favoriteNumber;          // 更新映射
        isRegistered[msg.sender] = true;

        emit PersonAdded(msg.sender, _name, _favoriteNumber);
    }

    /**
     * @dev 获取人员数量
     */
    function getPeopleCount() public view returns (uint256) {
        return people.length;
    }

    /**
     * @dev 仅owner可调用：重置存储的数字
     */
    function reset() public onlyOwner {
        emit NumberUpdated(msg.sender, storedNumber, 0);
        storedNumber = 0;
    }

    /**
     * @dev pure函数：不读取也不修改状态（类比静态工具方法）
     */
    function add(uint256 a, uint256 b) public pure returns (uint256) {
        return a + b;
    }
}
