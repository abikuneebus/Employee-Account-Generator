# Account Generator

### Table of Contents
1. [[#Introduction]]
2. [[#*About the Developer*|About the Developer & Contact]]
3. [[#Tech Stack Used]]
4. [[#Installation & Running]]
5. [[#GUI Components & Functionality]]
6. [[#Future Improvements *(hypothetical)*|Future Improvements]]
7. [[#*Closing Thoughts*|Closing Thoughts]]
8. [[#Screenshots]]

-------------------------------------------------------------------------
##### Introduction
I know that installing and configuring runtimes, setting environment variables, and the rest of the steps needed to run this app in a development environment is a very big ask. I also know that, for very good reason, nobody is going to unzip a stranger's demo application directory, let alone run an executable from within it. I went in depth on how to do both of the aforementioned things for posterity's sake, but I don't expect it from anyone. 
To hopefully serve as a middle-ground, I'm including screenshots that capture all the major functionality of the app. So as to not interrupt the flow of the `README`, I've inserted them at the bottom of the document, out of the way. If you want to jump straight to them, you can click [[#Screenshots|here]]. Thanks for your time!

-------------------------------------------------------------------------
###### *About the Developer*
I built 'Account Generator' because while I had become proficient with JavaScript, TypeScript, HTML, CSS, etc., I felt I needed to add native programming language experience to my repertoire. The initial idea for the application was a command line only utility app that would return an email address and randomly generated password as output given an "employee's" name and department, and was planned to be completed over a weekend. A few days later, I realized that I could not honestly call myself "comfortable" or even a novice at Java after creating such a simple app. I also had intended on diving deeper into object-oriented programming than I had with my web development endeavors, something the command line app wasn't capable of providing. Thus, I continued adding features.
Almost a month later, and the app had fulfilled its original purpose and then some. On top of becoming comfortable with Java and much better versed in OOP, I added SQL to my skillset. SQL was the next item on my list of technologies to learn, and an account generation app doesn't make much sense if it's not going to keep track of what it creates.

*Contact:*
brennan.obriencahill@gmail.com
GitHub.com/abikuneebus


-------------------------------------------------------------------------
#### Tech Stack Used

- Java (`JDK-20`) 
- JavaFX (`JavaFX-SDK-20.0.2`)
- Maven (`Apache-Maven-3.9.3`)
- SQLite (`SQLite3`)
- Launch4j (`Launch4j-3.5`)

-------------------------------------------------------------------------
## Installation & Running
#### Running Executable
- Running the executable is as easy as opening "AccountGenerator.exe", which can be found at `<path_to_directory>\AccountGenerator\AccountGenerator.exe`. No further setup is needed.
	- The `.exe` is dependent on the `.db` file stored in `<path_to_directory>\AccountGenerator\assets\db`—ensure that the application's position relative to the database *does not change*.
		- `AccountGenerator.exe` should be able to locate the database via relative path `\assets\db\accounts.db`.
#### Running From Development Environment
- I used VS Code for the development of this app, but I believe the instructions will be roughly the same regardless of platform; I believe development would have been easier had I gone with a full blown IDE like IntelliJ IDEA or Eclipse instead of a text-editor with IDE capabilities via extensions. There are a few prerequisites required to run this from a development environment, which aren't too difficult but will take a bit of time.

*Install & Configure Required Runtimes & Modules:*
1. Install JDK, set to `PATH`
	- [Documentation](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/)
	- [Download](https://www.oracle.com/java/technologies/downloads/)
2. Install Maven, set to `PATH`
	- [Documentation](https://maven.apache.org/install.html) 
	- [Download](https://maven.apache.org/download.cgi)
3. Install JavaFX Toolkit, set to `PATH`
	- [Documentation](https://openjfx.io/openjfx-docs/)
	- [Download](https://gluonhq.com/products/javafx/)
4. Install SQLite Toolkit, set to `PATH`
	- [Documentation](https://www.sqlitetutorial.net/download-install-sqlite/)
	- [Download](https://www.sqlite.org/download.html)

As the `<path_to_directory>\AccountGenerator\code` directory includes the project's `pom.xml`, you can download the project's dependencies with a few keystrokes in your preferred terminal:

5. navigate to the project's directory (absolute path to directory containing `pom.xml`):
```bash
cd local\directory\AccountGenerator
```

6. Install dependencies
```bash
mvn install
```

7. Compile & Run Application
```bash
mvn clean compile javafx:run
```


-------------------------------------------------------------------------
#### GUI Components & Functionality
- Login Screen:
	- SQL database comes pre-loaded with an IT administrator account's credentials. (credentials will display in an alert upon application launch)
		- The first time an invalid username or password is entered, an alert will warn "Invalid username or password, `10` attempts remaining." 
			- `loginTries = 0`, `triesRemaining = 10 - loginTries`, each invalid entry results in `loginTries++`, 
			- If a user enters an incorrect username and/or password 10 times, they will be "locked out of the account" (the app terminates execution, but no true lockout has been implemented).
				- If this were a production application, I'd implement a true 'account lockout' solution, as detailed in 'Future Improvements' below.
		- As with all user input fields in the app, if a `TextField` or `PasswordField` is empty, an 'empty field' alert will display.
			- An empty-field alert will *not* cause the login attempts counter to increment
		- If a valid username & password are entered, the login attempts counter is reset.
- Create New Account
	- After providing a first name, last name, and selecting a department, a new account will be generated and inserted into the database. This includes the names and department, as well as a randomly generated password that is hashed, an email address, and an (arbitrary) mailbox capacity.
	- **Creation/Validation Process**
		- **Name/Username**
			- Users may use all alphabetical characters, hyphens, apostrophes, and a single whitespace when entering their name. 
				- *Once a valid name is entered, a username is created. By default, it is `firstnameLastname` (johnSmith)*.
					- If John Smith creates an account after another John Smith has already created one, the user will be prompted to enter an alternate username. Their input is validated with the same method that validates the automatically generated username; this process loops until a valid & non-duplicate username is created.
					- *The created username is then appended with the users domain, which is parameterized using their department, a period "`.`" (only `if department != "N/A"`) and the company name* plus ".com".
		- **Password Validation Process**
			- Once a valid first name, last name, department, and username have been obtained, the `Email` object is created, which includes the automatic generation of a strong password.
				- The generated password must pass the same validation as passwords entered in the 'Change Password' menu. This will run recursively until a valid password is created—it is then hashed.
					- If this were a production application, this would be a 'one-time code' that would be given to the user to be used to set their own password, as detailed in 'Future Improvements' below.
- Modify Existing Account
	- Once you've created an account for the "new employee", you can navigate back to the main menu and change specifics, if necessary. Currently, the modifiable fields include only first name, last name, and mailbox capacity. These fields could be any number of different things in a production application (sales data, accounting, customer information, etc.).
- Change Password
	- If you'd like to change the account's password, you can select the 'Change Password' button from the 'Modify Existing Account' menu. The password must be robust; the specific requirements can be seen in tooltips that drop down from each password-field on mouse hover.
		- In a production application, this would be  much more robust. I thoroughly cover the hypothetical improvements I'd make below, in 'Future Improvements'.
- Delete Account
	- Of course, the app would not be complete without the ability to delete an account. This is also accomplished from the Modify Existing Account menu, and will purge an entire account from the database upon confirmation of intent.


#### Future Improvements *(hypothetical)*
- Add `administrator` field to `email_accounts` table
	- Restrict 'Create New Account' & 'Modify Existing Account' to accounts with value of `true` for `adminstrator` field
		- Limits access of sensitive account information to trusted administrators
- Allow limited access to accounts from any department to change their password
	- (if department != null && department != "IT") {launch 'Change Password' menu}
		- After user changes password, terminate app (no access to other accounts)
- Add 'forgot password' functionality
	- Add 'forgot password' button to login screen
		- (add `hashedOneTimeCode` field to `email_accounts` table)
		- Send one-time code to email associated with username, encrypt `oneTimeCode` and update account's `hashedOneTimeCode` field
			- Use BCrypt to validate `oneTimeCodeInput` user input matches `hashedOneTimeCode`
				- Launch 'Change Password' menu
					- After user changes password, terminate app (no access to other accounts)
- Add true "account lockout".
	- Add `int loginTries` field to all accounts.
		- Instead of incrementing `loginTries` within the login class, add `loginTries <= 10` check to `verifyCredentials` (currently `loginTries` resets upon app execution, rendering it moot)
			- `if (loginTries <= 10) {` display message directing user towards an administrator, or implement an account recovery method (security question/alternate email/phone number)

-------------------------------------------------------------------------
#### *Closing Thoughts*
There are many things I'd do differently if I were to start from the ground up to create this app. This process in itself was a great example of why systems like Agile are crucial. If I had started from the beginning knowing that the app would have a GUI, I'd have implemented a Model-View-Controller pattern. If I had been strictly adhering to an Agile-like system, I'd have immediately refactored the app to fit the MVC model, as by the time I was thinking about implementing it, it would have been a gargantuan task.

I also learned more about object-oriented programming than I did in 5 months working on a TypeScript API rebuild; likely because I was working for someone who had taken care of the dev-ops side of the house.

If you are reading this, I cannot express enough how grateful I am for your time. This app is far from perfect, but I hope it speaks to a level of proficiency in software development that would translate well to an entry-level role. 

Best,
Brennan

### Screenshots
![Login-Demo](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/800fddc1-0ad3-4d91-bea8-17d6dc10073c)

![Login-Values](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/ad5b67d3-76a3-4e60-bbdc-f05a172d0580)

![Login-EFError](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/f6d96f0d-23b9-4c07-8acb-76c2eadc6fe5)

![Login-CVError](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/431f2469-ba59-4300-81a6-db6df67f6570)

![Login-TempLockout](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/1172c145-6b33-4676-a32c-06586cd03b76)

![AddOrMod](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/8cb6c4ca-73f7-427c-ae99-949da8d7aa89)

![CreateNewAccount-Dept](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/15b28aaa-1421-4d56-8943-dda7320764d3)

![UserSearch-Tooltip](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/a5fa6a1b-9c4e-4bf5-8b2f-e75fc54cd1ad)

![UserSearch-johnnyA](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/68040bee-6638-41ab-a5ca-bbeb0e5b6fdf)

![ModifyAccountMenu-johnnyA](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/d498a986-cdfc-47c0-bc13-c7c74d43174d)

![ModifyAccountMenu-mailCap](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/f74bc736-f963-4e0d-8a38-db3d6f7243c7)

![ChangePW-Tooltip](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/29eb5844-7d13-40f5-8c79-72d9e6235fcd)

![ChangePW-SCalt](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/37281859-e35b-4ae3-a21a-73e546687a92)

![ChangePW-UCalt](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/a2113f98-07b5-461c-b00c-e0c52c193abc)

![ChangePW-Lengthalt](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/fe035250-4ab6-464f-b420-becdc9e41f27)

![SQLiteStudio-selectDB](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/276abf96-5266-42fb-9a66-dc8f7c922d06)

![SQLiteStudio-dataWide](https://github.com/abikuneebus/Employee-Account-Generator/assets/113397554/3ad15820-1867-411c-8995-2ef1e98265dd)
