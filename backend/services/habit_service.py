import json
import os
from typing import List, Optional
from schemas.habit import HabitCreate, HabitUpdate, HabitRead

DB_FILE = "habits.json"

def _load_habits() -> List[dict]:
    if not os.path.exists(DB_FILE):
        return []
    with open(DB_FILE, "r", encoding="utf-8") as f:
        try:
            return json.load(f)
        except json.JSONDecodeError:
            return []

def _save_habits(habits: List[dict]):
    with open(DB_FILE, "w", encoding="utf-8") as f:
        json.dump(habits, f, indent=4)

def get_all_habits() -> List[HabitRead]:
    habits = _load_habits()
    return [HabitRead(**habit) for habit in habits]

def get_habit_by_id(habit_id: int) -> Optional[HabitRead]:
    habits = _load_habits()
    for habit in habits:
        if habit["id"] == habit_id:
            return HabitRead(**habit)
    return None

def create_habit(habit_data: HabitCreate) -> HabitRead:
    habits = _load_habits()
    new_id = max((habit["id"] for habit in habits), default=0) + 1
    new_habit = habit_data.model_dump()
    new_habit["id"] = new_id
    habits.append(new_habit)
    _save_habits(habits)
    return HabitRead(**new_habit)

def update_habit(habit_id: int, habit_data: HabitUpdate) -> Optional[HabitRead]:
    habits = _load_habits()
    for i, habit in enumerate(habits):
        if habit["id"] == habit_id:
            update_data = habit_data.model_dump(exclude_unset=True)
            updated_habit = {**habit, **update_data}
            habits[i] = updated_habit
            _save_habits(habits)
            return HabitRead(**updated_habit)
    return None

def delete_habit(habit_id: int) -> bool:
    habits = _load_habits()
    original_len = len(habits)
    habits = [h for h in habits if h["id"] != habit_id]
    if len(habits) < original_len:
        _save_habits(habits)
        return True
    return False