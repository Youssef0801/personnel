CREATE TABLE Ligue (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL
);

CREATE TABLE Employe (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    mail VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('employe', 'admin', 'superadmin') NOT NULL,
    dateArrivee DATE NOT NULL,
    dateDepart DATE NULL,
    ligue_id INT NULL,
    FOREIGN KEY (ligue_id) REFERENCES Ligue(id) ON DELETE SET NULL,
    CHECK (dateDepart IS NULL OR dateDepart >= dateArrivee)
);


