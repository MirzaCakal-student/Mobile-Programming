from pydantic import BaseModel
from typing import Optional

class HabitBase(BaseModel):
    title: str
    description: str
    frequency: str
    completed: bool = False

class HabitCreate(HabitBase):
    user_id: int

class HabitUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    frequency: Optional[str] = None
    completed: Optional[bool] = None
    user_id: Optional[int] = None

class HabitRead(HabitBase):
    id: int
    user_id: int

    class Config:
        from_attributes = True