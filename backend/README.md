# Dummy Tags API

A simple FastAPI backend for managing tags with local JSON storage.

## Features
- CRUD operations for Habits (id, name, description, user_id).
- Hardcoded header authentication.
- Local JSON storage (`habits.json`).

## Setup

1. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

2. **Run the server:**
   ```bash
   uvicorn main:app --reload
   ```
   or
   ```bash
   python -m uvicorn main:app --reload
   ```
   or
   ```bash
   python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
   ```

## Usage

All endpoints (except root) require the following header:
`X-Authentication: yes`

### Endpoints
- `GET /habits/`: List all tags.
- `POST /habits/`: Create a new tag.
- `GET /habits/{habit_id}`: Get tag details.
- `PUT /habits/{habit_id}`: Update a tag.
- `DELETE /habits/{habit_id}`: Delete a tag.

### Example Create Request
> **Note:** The `id` is automatically generated based on the current number of tags (`size + 1`).

```bash
curl -X POST "http://127.0.0.1:8000/habits/" \
     -H "X-Authentication: yes" \
     -H "Content-Type: application/json" \
     -d '{"name": "Urgent", "description": "High priority tasks", "user_id": 1}'
```

### Important Notes
- **Request Body:** Always send data as **JSON**. Sending form-data or URL-encoded data will result in a 422 Unprocessable Entity error.
- **Header:** The `X-Authentication: yes` header is mandatory for all `/habits/` endpoints.

