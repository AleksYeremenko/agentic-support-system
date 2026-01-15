# Agentic Support System (Java)

A custom multi-agent support system built with **Java 21** and **Llama 3.3** (via Groq API). This project demonstrates a robust orchestration of AI agents for technical and billing support without using high-level frameworks like LangChain.

## 🚀 Key Architectural Features

- **Dual-Agent Orchestration**: Intelligent routing between **Agent A** (Technical Specialist) and **Agent B** (Billing Specialist).
- **Custom RAG Engine**: A lightweight Retrieval-Augmented Generation system in `KnowledgeBase` that scores and fetches context from local documentation.
- **Secure Function Calling**: A regex-based command parser that allows the LLM to trigger Java methods (e.g., creating tickets, checking refunds) safely.
- **Production-Ready Patterns**:
    - **Centralized Config**: All constants and safety thresholds are managed in `AppConfig`.
    - **Custom Exception Handling**: Uses `AppException` for robust error management.
    - **Defensive Programming**: Strict input validation to prevent LLM hallucinations from affecting business logic.

## 🛠 Tech Stack
- **Language**: Java 21
- **JSON Processing**: Jackson Databind
- **Environment Management**: dotenv-java
- **Logging**: SLF4J (Simple)
- **AI Model**: Llama-3.3-70b-versatile

## ⚙️ Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone <your-repository-link>