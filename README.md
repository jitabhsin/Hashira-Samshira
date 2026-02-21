# Polynomial Secret Solver

A Spring Boot application built in Java that decodes and solves a polynomial secret based on Shamir's Secret Sharing algorithm. The application reads polynomial roots provided in a JSON format (where values are encoded in various bases), decodes them, and calculates the constant term (the secret) using Lagrange Interpolation with exact BigInteger arithmetic.

## Prerequisites
* **Java 21** or higher
* **Maven** for dependency management
* Your preferred IDE (Eclipse, IntelliJ IDEA, VS Code)

STEPS :
1. clone the repo 
2. mvn clean install
3. mvn spring-boot:run
4.=======================================================
  SUCCESS! THE DECODED SECRET VALUE IS: [your_secret_here]
=======================================================


## Project Structure
Ensure your test case file is named `testcase.json` and is placed exactly in the `resources` directory before running:

```text
src/
 └── main/
      └── resources/
           └── testcase.json


