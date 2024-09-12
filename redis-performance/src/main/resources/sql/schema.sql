DROP TABLE IF EXISTS product;

CREATE TABLE product (
                         id INT AUTO_INCREMENT PRIMARY KEY,   -- SERIAL 대신 INT AUTO_INCREMENT 사용
                         description VARCHAR(500),
                         price DECIMAL(10,2) NOT NULL         -- NUMERIC 대신 DECIMAL 사용
);