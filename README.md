## Telegram File Storage Bot

### Project Description
A Telegram bot designed for saving files to a remote database and generating download links for specified photos or documents.

### Application Workflow
#### 1. Bot Startup
The user launches the bot. By using the `/help` command, the user receives a list of available commands.

#### 2. User Registration
When the `/register` command is entered or selected from the `/help` menu, the bot prompts the user to provide an email address for registration. The email address is validated to match the syntax: `name@domain.top-level-domain`.

#### 3. Registration Completion
If the email address passes validation, a confirmation link is sent to the provided email. After clicking the link, the user is assigned the `BASIC_STATE`, which allows access to all bot features.

#### 4. Content Upload
When a photo or document is sent to the bot, the file is converted into binary data and stored in a database on a remote server. After processing, the bot generates an external link for downloading the uploaded file.

### Technologies Used
- **Telegram Long Polling**: Enables simplified interaction between the client and Telegram servers without requiring a domain name. Ideal for personal or small-scale applications.
- **PostgreSQL**: A classic solution for storing user data and files in binary format.
- **RabbitMQ**: The application does not require the advanced scalability or high throughput provided by Apache Kafka. Instead, RabbitMQ is used as a lightweight message broker, supporting request-response scenarios.
- **REST API**: Used for user activation and providing endpoints for file and photo downloads.
- **Java Mail Sender**: Responsible for generating and sending confirmation emails.

### Project Structure
#### 1. Dispatcher
A microservice for initial validation of incoming data and routing it to appropriate RabbitMQ queues:

- `text-message-update`
- `doc-message-update`
- `photo-message-update`
- `answer-message`

#### 2. Node Service
The core service containing the project's main business logic. It processes messages from RabbitMQ and generates responses for the user. This service is horizontally scalable to allow parallel data processing if needed.

#### 3. REST Service
A RESTful API service responsible for:
- Processing incoming HTTP requests for file downloads.
- Handling email address confirmation.

#### 4. Mail Service
A microservice for sending confirmation emails that include a link for user registration verification.

