CREATE TABLE Ligue (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL
);

CREATE TABLE Employe (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    mail VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    dateArrivee DATE,
    dateDepart DATE,
    ligue_id INT,
    FOREIGN KEY (ligue_id) REFERENCES Ligue(id)
);
