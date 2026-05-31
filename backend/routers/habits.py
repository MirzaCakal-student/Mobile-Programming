from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from schemas.habit import HabitRead, HabitCreate, HabitUpdate
from services import habit_service
from dependencies import verify_authentication_header

router = APIRouter(
    prefix="/habits",
    tags=["habits"],
    dependencies=[Depends(verify_authentication_header)]
)

@router.get("/", response_model=List[HabitRead])
async def list_habits():
    return habit_service.get_all_habits()

@router.post("/", response_model=HabitRead, status_code=status.HTTP_201_CREATED)
async def create_habit(habit_data: HabitCreate):
    return habit_service.create_habit(habit_data)

@router.get("/{habit_id}", response_model=HabitRead)
async def get_habit(habit_id: int):
    habit = habit_service.get_habit_by_id(habit_id)
    if not habit:
        raise HTTPException(status_code=404, detail="Habit not found")
    return habit

@router.put("/{habit_id}", response_model=HabitRead)
async def update_habit(habit_id: int, habit_data: HabitUpdate):
    habit = habit_service.update_habit(habit_id, habit_data)
    if not habit:
        raise HTTPException(status_code=404, detail="Habit not found")
    return habit

@router.delete("/{habit_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_habit(habit_id: int):
    if not habit_service.delete_habit(habit_id):
        raise HTTPException(status_code=404, detail="Habit not found")
    return None