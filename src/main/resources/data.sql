CREATE TABLE Medication (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    weight FLOAT,
    code VARCHAR(100),
    image VARCHAR(100)
);



INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_1', 11.23, 'CODE1', 'src/main/resources/image1.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication-2', 12.34, 'CODE2', 'src/main/resources/image2.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_3', 3.45, 'CODE3', 'src/main/resources/image3.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_4', 14.56, 'CODE4', 'src/main/resources/image4.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_5', 5.67, 'CODE5', 'src/main/resources/image5.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_6', 26.78, 'CODE6', 'src/main/resources/image6.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_7', 17.89, 'CODE7', 'src/main/resources/image7.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_8', 8.90, 'CODE8', 'src/main/resources/image8.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_9', 9.01, 'CODE9', 'src/main/resources/image9.jpg');

INSERT INTO Medication (name, weight, code, image)
VALUES ('Medication_10', 10.12, 'CODE10', 'src/main/resources/image10.jpg');
