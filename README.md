# research-assistant

# Smart Research & Content Companion (Backend)

## Project Goal
To build the backend API for a personal knowledge assistant that allows users to save, process, and intelligently query web content and documents.

## Core Features (Phase 1 - Backend)
- **User Management:** (Simplified for now) Basic user setup.
- **Content Ingestion:** API endpoint to receive a URL, fetch its content, and store metadata.
- **AI Processing:** Integration with an AI model to summarize and extract keywords from ingested content.
- **Semantic Storage:** Store processed content in Elasticsearch for efficient retrieval.
- **Q&A Endpoint:** API endpoint to ask a question, retrieve relevant context from Elasticsearch, and use an AI model to generate an answer based on that context.

## Technology Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Build Tool:** Maven or Gradle (We'll decide together)
- **Database:**
    - **SQL Database:** MySQL (for production) / H2 (for development) - for user data and content metadata (links, titles, etc.).
    - **Search Engine:** Elasticsearch - for storing and searching the processed text content, summaries, and topics.
- **AI Integration:** OpenAI API (or a local alternative like Ollama for development/free tier).
- **Other Libraries:** Spring Data JPA, Spring Web, Spring AI (if we choose to use it), Elasticsearch Java Client.

## Project Structure (Backend)
src/
├── main/
│ ├── java/com/example/smartresearch/
│ │ ├── controller/ # REST API endpoints
│ │ ├── service/ # Business logic
│ │ ├── repository/ # Database interaction (JPA)
│ │ ├── model/ # Entity and DTO classes
│ │ ├── config/ # Configuration classes (e.g., AI, Elasticsearch)
│ │ └── util/ # Helper classes (e.g., content fetcher)
│ └── resources/
│ └── application.yml # Application configuration
└── test/ # Unit and integration tests


## Weekly Progress Log
- **Weekend 1 (Feb 15, 2026):** Initial project setup, basic Spring Boot application with a health check API, and H2 database configuration.
- **Weekend 2:**
- **Weekend 3:**
- **...**

