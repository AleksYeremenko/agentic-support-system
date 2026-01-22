# Agentic Support System (Java)

A professional, autonomous multi-agent support system built with **Java 21** and **Llama 3.3** (via Groq API). This project demonstrates advanced AI orchestration, featuring persistent memory, session security, and a hybrid RAG engine.

## 🚀 Key Architectural Features

* **Multi-Agent Collaboration**: Intelligent routing between **Agent A** (Technical) and **Agent B** (Billing). The system maintains context when switching agents during a single conversation.
* **Persistent Memory (Stateful)**: Uses a JSON-based session manager to remember user details (like names or previous issues) even after the application restarts.
* **Hybrid RAG Engine**:
    * **Context Retrieval**: Scores local `.txt` documents for relevance using synonym expansion.
    * **Fallback Intelligence**: If documentation is missing, agents use general technical/billing knowledge with appropriate disclaimers, preventing "I don't know" dead-ends.
* **Enterprise Safety & UX**:
    * **Session Timeout**: Automatically closes the connection after 120 seconds of inactivity for security.
    * **Proactive Greeting**: Agents initiate the conversation to improve user engagement.
    * **Boundary Control**: Intent classifier strictly blocks non-support queries (e.g., cooking, off-topic chat).
* **Tool Calling**: Native implementation of LLM tool execution for creating tickets, checking refund eligibility, and plan verification.

## 🛠 Tech Stack
* **Language**: Java 21
* **AI Model**: Llama-3.3-70b-versatile (Groq)
* **JSON Processing**: Jackson Databind
* **Environment Management**: dotenv-java
* **Logging**: SLF4J (Simple)

## 🏗 Project Structure
* `SupportOrchestrator`: The brain of the system; manages intent and agent switching.
* `KnowledgeBase`: Custom search engine with weighted scoring for local docs.
* `ChatSessionManager`: Handles persistence of conversation history in `data/history.json`.
* `TicketService`: Mock business logic for ticket creation and billing checks.

## ⚙️ Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone <your-repository-link>