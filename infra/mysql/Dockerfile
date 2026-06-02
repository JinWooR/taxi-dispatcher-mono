FROM mysql:8.0

# my.cnf를 이미지에 포함 (Windows 마운트 시 권한 문제 회피)
# world-writable이 아닌 read-only 권한으로 설정하여 MySQL이 무시하지 않도록 함
COPY my.cnf /etc/mysql/conf.d/charset.cnf

RUN chmod 0444 /etc/mysql/conf.d/charset.cnf \
 && chown root:root /etc/mysql/conf.d/charset.cnf
