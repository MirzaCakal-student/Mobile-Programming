from fastapi import FastAPI
from routers import habits

app = FastAPI(title="Dummy Habits API")

app.include_router(habits.router)

@app.get("/")
async def root():
    return {"message": "Welcome to the Dummy Habits API"}