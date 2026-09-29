# Flight Reservations

This is a console application for managing airlines, customers, flights and bookings. It was built for **Challenge 2.1 Connect** in the Data Access module (2DAMi).

Authors: Brayan, Ekaitz, Aritz.

## How the data is stored

| Entity | Storage | Access |
| --- | --- | --- |
| Airline, Customer, Flight | MySQL 8.0 database `airlinedb` | JDBC (`AirlineDAOImpl`, `CustomerDAOImpl`, `FlightDAOImpl`) |
| Booking (Customer ↔ Flight) | One binary `flights.dat` file per customer | Java I/O streams (`BookingDAOImpl`) |

Each customer's bookings file is opened from the path stored in the database (`customer.path`, for example `data/customers/1/flights.dat`). The app generates that path when the customer is registered and saves it in the same transaction as the customer.

## Architecture

```text
views (console)  ──►  service (business rules)  ──►  dao (interfaces)  ──►  dao.impl  ──►  MySQL / flights.dat
```

- **views**: `ConsoleMenu`, `ConsoleInput` and `ConsolePrinter`. This layer reads input, shows results, and is where every error ends up being shown.
- **service**: `AirlineService`, `CustomerService`, `FlightService` and `BookingService`. They validate input and apply the booking rules. They only use the DAO interfaces.
- **dao**: the `AirlineDAO`, `CustomerDAO`, `FlightDAO` and `BookingDAO` interfaces.
- **dao.impl**: one implementation per interface, plus `DAOFactory`. `DAOFactory` is a Singleton and the only class that creates the DAOs.
- **config**: `ConnectionManager`. It's a Singleton that loads `config/db.properties` once and opens a new connection for each operation. Every connection is closed with try-with-resources.
- **exception**:
  - `DataAccessException` (and its subclass `DuplicateEntryException`) for data-layer failures.
  - `BusinessException` and its subclasses for broken rules: `ValidationException`, `EntityNotFoundException`, `FlightDepartedException`, `DuplicateBookingException` and `NoSeatsAvailableException`.
- **util**: `InputValidator`, which holds every input rule.

See [docs/IMPLEMENTATION_PLAN.md](docs/IMPLEMENTATION_PLAN.md) for the full design.

## Requirements

- JDK 17 or newer
- MySQL Server 8.0
- MySQL Connector/J 9.7.0, already included in `lib/`

## Setup

1. **Create the database.** Run [database/airlinedb_script.sql](database/airlinedb_script.sql).
   - In MySQL Workbench: *File → Open SQL Script*, then *Execute*.
   - From a terminal: `mysql -u root -p < database/airlinedb_script.sql`

   The script drops and recreates `airlinedb` and loads the sample data.
2. **Set your credentials.** Copy `config/db.properties.example` to `config/db.properties` and set `db.user` and `db.password`. Git ignores this file, so your password is never committed.
3. **Compile and run** from the project root. Run from the root so the relative paths `config/` and `data/` resolve.

   PowerShell:

   ```powershell
   javac --release 17 -encoding UTF-8 -d bin -cp "lib/*" (Get-ChildItem -Recurse src -Filter *.java).FullName
   java -cp "bin;lib/*" flightreservations.App
   ```

   Bash:

   ```bash
   javac --release 17 -encoding UTF-8 -d bin -cp "lib/*" $(find src -name "*.java")
   java -cp "bin:lib/*" flightreservations.App
   ```

   In VS Code you can also open `src/flightreservations/App.java` and click *Run*.

## Sample data

| Customer | Upcoming flights | Past flights |
| --- | --- | --- |
| 1 Laura García | 4 Bilbao → London, 5 Madrid → New York | 1 Bilbao → Madrid |
| 2 Mikel Etxeberria | 8 Bilbao → Barcelona | 3 Barcelona → Berlin, 2 Madrid → Paris |
| 3 John Smith | 7 Paris → Frankfurt | 1 Bilbao → Madrid |
| 4 Marie Dubois | none (no bookings file yet) | none |

The script sets flight dates relative to the day it runs, so past and future flights never go stale. Two flights are set up for demos:
- Flight 6 (Barcelona → Rome) has **1 seat left**.
- Flight 7 (Paris → Frankfurt) is **full**.

**Resetting the data.** Re-running the SQL script resets the database but not the booking files. To start from scratch:
1. Run `database/airlinedb_script.sql`.
2. Run `java -cp "bin;lib/*" flightreservations.SampleDataSeeder` (use `bin:lib/*` in Bash). This rebuilds `data/customers/*/flights.dat` from the database paths.

## Bookings file format

Each `flights.dat` file is a sequence of fixed 12-byte records written with `DataOutputStream`:

| Field | Type | Bytes |
| --- | --- | --- |
| flightId | `int` | 4 |
| bookingDate | `long` (days since 1970-01-01) | 8 |

A missing file means the customer has no bookings yet. The app creates the file and its folder on the first booking. If a file's size isn't a multiple of 12, the app reports it as corrupted.

## Use cases

| Menu | Use case | Rules |
| --- | --- | --- |
| 1 | Register a customer | The name is required. The email must be valid and not already registered. The phone must have 9–20 digits. |
| 2 | Check a customer's flights | Shows booked flights departing today or later. |
| 3 | View a customer's flight history | Shows booked flights that departed before today. |
| 4 | Register an airline | The IATA code must be 2 letters or digits and not already registered. |
| 5 | Register a flight | The airline must exist. Origin and destination must differ. The date must be after today and at most 1 year ahead. Seats must be 1–850. |
| 6 | Check future flights | Shows flights departing after today. |
| 7 | Book a flight | The flight must depart after today, must not already be booked by the customer, and must have a seat left. |

**Booking consistency.** A booking first takes a seat with one atomic `UPDATE ... WHERE seatAmount > 0`, so two bookings can't take the same last seat. It then appends the booking to the customer's file. If the file write fails, the app gives the seat back.

## Manual verification checklist

- [ ] The script runs without errors, and the tables have 5 airlines, 4 customers and 8 flights.
- [ ] The DB rejects a duplicate IATA code, a flight with an unknown `airlineId`, and `seatAmount = -1`.
- [ ] The seeder writes 36 bytes for customers 1 and 2, and 24 bytes for customer 3.
- [ ] Register an airline. A duplicate IATA code (for example `IB`) shows a warning.
- [ ] Register a customer. The new row has `path = data/customers/<id>/flights.dat`. A duplicate email shows a warning.
- [ ] Register a flight. A past date, the same origin and destination, or 0 seats are re-prompted.
- [ ] Check future flights. Airline names are shown.
- [ ] Book flight 6 for customer 4:
  - The booking works and flight 6's seats drop to 0.
  - `data/customers/4/flights.dat` is created with 12 bytes.
- [ ] Booking errors show a warning:
  - flight 6 again (no seats);
  - flight 4 for customer 1 (already booked);
  - flight 1 (already departed);
  - customer or flight 999 (does not exist).
- [ ] Customer's flights and history show the split in the sample data table. Customer 4 shows "no past flights".
- [ ] Letters in a number field, a bad date format, or an empty name are re-prompted.
- [ ] With MySQL stopped (`MySQL80` service), a data option shows `[ERROR] Cannot connect...` and the menu keeps running.
- [ ] With `config/db.properties` missing, a data option shows the setup message.
- [ ] Ctrl+Z (Windows) or Ctrl+D (Linux/macOS) ends the app cleanly.

## Troubleshooting

| Problem | Fix |
| --- | --- |
| `Database settings not found` | Copy `config/db.properties.example` to `config/db.properties`. |
| `The MySQL JDBC driver was not found` | Add `lib/*` to the classpath (see the run commands). |
| `Public Key Retrieval is not allowed` | Keep `allowPublicKeyRetrieval=true&useSSL=false` in `db.url` (local server only). |
| The banner shows `?` characters | Your console code page lacks those symbols. Run `chcp 65001` first, or ignore it. |
| Bookings are not found | Run the app from the project root. The file paths are relative to it. |

## Project structure

```text
config/db.properties.example   template for the local credentials
data/customers/<id>/flights.dat sample booking files
database/airlinedb_script.sql  database creation + sample data
docs/                          challenge statement, rubric, implementation plan
lib/                           MySQL Connector/J
src/flightreservations/        App, SampleDataSeeder and the layer packages
```
