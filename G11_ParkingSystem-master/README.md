🚗 G11 Parking System – Smart Parking Management
A full-stack Java-based smart parking management system developed as part of an academic project. This application allows real-time management and monitoring of parking reservations, availability, subscriber details, and vehicle activity through a client-server architecture.

🔧 Technologies Used:

-Java 17
-JavaFX (GUI)
-MySQL (Database)
-OCSF (Client-Server Communication)
-JDBC (Database Integration)
-Eclipse IDE
-Git (Version Control)

📦 Features:

-User & Subscriber Management (Registration, Login, Update)
-Real-time Public Parking Availability
-Reservation Handling (Book, Extend, Cancel)
-Vehicle Entry/Exit with Time Validation
-Admin Tools for Managing Subscribers & Spaces
-Server-Side Database Connection Pooling
-Fully Interactive JavaFX GUI

📁 Structure:

-ParkingSystemClient: JavaFX-based GUI
-ParkingSystemServer: OCSF-based backend server
-bpark_common: Shared data structures for Client-Server communication
-bpark_db: MySQL schema and scripts

🧠 Designed With:

-Singleton & Factory Patterns
-Separation of Concerns between UI, Logic, and Data

📘 How to Run:

-Import the project into Eclipse
-Configure the db.properties file with your MySQL credentials
-Start the server (ParkingSystemServer)
-Launch the client (ParkingSystemClient)
-Use test users or register a new one

