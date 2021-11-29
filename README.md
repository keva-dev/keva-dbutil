# Keva DB Util

## Usage
- Get the `kdb` file from KevaDB
```shell
cd keva-dbutil
./gradlew run --args="./dump.kdb ./test.rdb"
```
- Verify the result `rdb` using redis or [redis-rdb-tools](https://github.com/sripathikrishnan/redis-rdb-tools)

- Use `--help` for more info
```shell
./gradlew run --args="--help"
```