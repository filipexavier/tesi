CREATE USER 'tesi'@'localhost'
IDENTIFIED BY 'tesi';

CREATE DATABASE tesi;

GRANT ALL ON tesi.* TO 'tesi'@'localhost'; 
