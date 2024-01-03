
## [Chỉ chạy lần đầu]

```console
docker pull mysql/mysql-server:5.7

docker network create demo-multi-ds-group-net

docker run -d --name=master-demo-multi-ds -p 33312:3306 --net=demo-multi-ds-group-net --hostname=master-demo-multi-ds ^
  -v %cd%/demo-multi-ds1:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root ^
  mysql/mysql-server:5.7 ^
  --server-id=1 ^
  --log-bin="mysql-bin-1.log" ^
  --enforce-gtid-consistency="ON" ^
  --log-slave-updates="ON" ^
  --gtid-mode="ON" ^
  --transaction-write-set-extraction="XXHASH64" ^
  --binlog-checksum="NONE" ^
  --master-info-repository="TABLE" ^
  --relay-log-info-repository="TABLE" ^
  --plugin-load="group_replication.so" ^
  --relay-log-recovery="ON" ^
  --loose-group-replication-start-on-boot="OFF" ^
  --loose-group-replication-group-name="aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee" ^
  --loose-group-replication-local-address="master-demo-multi-ds:6606" ^
  --loose-group-replication-group-seeds="master-demo-multi-ds:6606,slave-demo-multi-ds:6606" ^
  --loose-group-replication-single-primary-mode="ON" ^
  --loose-group-replication-enforce-update-everywhere-checks="OFF"

docker run -d --name=slave-demo-multi-ds -p 33313:3306 --net=demo-multi-ds-group-net --hostname=slave-demo-multi-ds ^
  -v %cd%/demo-multi-ds2:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root ^
  mysql/mysql-server:5.7 ^
  --server-id=2 ^
  --log-bin="mysql-bin-1.log" ^
  --enforce-gtid-consistency="ON" ^
  --log-slave-updates="ON" ^
  --gtid-mode="ON" ^
  --transaction-write-set-extraction="XXHASH64" ^
  --binlog-checksum="NONE" ^
  --master-info-repository="TABLE" ^
  --relay-log-info-repository="TABLE" ^
  --plugin-load="group_replication.so" ^
  --relay-log-recovery="ON" ^
  --loose-group-replication-start-on-boot="OFF" ^
  --loose-group-replication-group-name="aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee" ^
  --loose-group-replication-local-address="slave-demo-multi-ds:6606" ^
  --loose-group-replication-group-seeds="master-demo-multi-ds:6606,slave-demo-multi-ds:6606" ^
  --loose-group-replication-single-primary-mode="ON" ^
  --loose-group-replication-enforce-update-everywhere-checks="OFF"

docker exec -t master-demo-multi-ds mysql -uroot -proot ^
  -e "SET @@GLOBAL.group_replication_bootstrap_group=1;" ^
  -e "create user 'repl'@'%';" ^
  -e "GRANT REPLICATION SLAVE ON *.* TO repl@'%';" ^
  -e "flush privileges;" ^
  -e "change master to master_user='root' for channel 'group_replication_recovery';" ^
  -e "START GROUP_REPLICATION;" ^
  -e "SET @@GLOBAL.group_replication_bootstrap_group=0;" ^
  -e "SELECT * FROM performance_schema.replication_group_members;"

docker exec -t slave-demo-multi-ds mysql -uroot -proot ^
  -e "change master to master_user='repl' for channel 'group_replication_recovery';" ^
  -e "START GROUP_REPLICATION;"

docker exec -it master-demo-multi-ds /bin/bash

mysql -u root -p

create user 'root'@'%' identified by 'root';

grant all privileges on *.* to 'root'@'%' with grant option;

flush privileges;
```

## [Chạy mỗi khi khởi động lại containers database]

```console
docker exec -t master-demo-multi-ds mysql -uroot -proot ^
  -e "SET @@GLOBAL.group_replication_bootstrap_group=1;" ^
  -e "START GROUP_REPLICATION;"

docker exec -t slave-demo-multi-ds mysql -uroot -proot ^
  -e "START GROUP_REPLICATION;"
  
docker exec -t master-demo-multi-ds mysql -uroot -proot ^
  -e "SHOW VARIABLES WHERE Variable_name = 'hostname';" ^
  -e "SELECT * FROM performance_schema.replication_group_members;"
```
Hiển thị ra như bên dưới là thành công
```
+---------------------------+--------------------------------------+----------------------+-------------+--------------+
| CHANNEL_NAME              | MEMBER_ID                            | MEMBER_HOST          | MEMBER_PORT | MEMBER_STATE |
+---------------------------+--------------------------------------+----------------------+-------------+--------------+
| group_replication_applier | f0bfa5cd-aa0c-11ee-a878-0242ac150002 | master-demo-multi-ds |        3306 | ONLINE       |
| group_replication_applier | f622a599-aa0c-11ee-a9a0-0242ac150003 | slave-demo-multi-ds  |        3306 | ONLINE       |
+---------------------------+--------------------------------------+----------------------+-------------+--------------+
```

## [Tạo database (kết nối vào master)]

```mysql
CREATE DATABASE `demo-multi-ds`;

USE `demo-multi-ds`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

## [Demo]

```
curl --location 'localhost:8080/api/user/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "emailtest@email.vn",
    "password": "pwtest",
    "name": "name test",
    "address": "address test",
    "phone_number": "0987654321"
}'
```

```
curl --location 'localhost:8080/api/user'
```

```
curl --location 'localhost:8080/api/user/update' \
--header 'Content-Type: application/json' \
--data '{
    "id": "84464295-aa1b-11ee-973e-c3be5326c02b",
    "address": "address test 3",
    "phone_number": "0987654321"
}'
```

```
curl --location 'localhost:8080/api/user/delete' \
--header 'Content-Type: application/json' \
--data '{
    "id": "84464295-aa1b-11ee-973e-c3be5326c02b"
}'
```

