from sqlalchemy import Column, Integer, Float, String, ForeignKey, DateTime
from sqlalchemy.sql import func

from database import Base


class Student(Base):
    __tablename__ = "students"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    email = Column(String(150), unique=True, nullable=False)
    password = Column(String(255), nullable=False)
    created_at = Column(
        DateTime,
        server_default=func.now()
    )


class AcademicData(Base):
    __tablename__ = "academic_data"

    id = Column(Integer, primary_key=True, index=True)

    student_id = Column(
        Integer,
        ForeignKey("students.id", ondelete="CASCADE"),
        nullable=False
    )

    previous_score = Column(Float)
    study_hours = Column(Float)
    quiz_average = Column(Float)
    quizzes_attempted = Column(Integer)
    previous_exam_score = Column(Float)
    learning_activity = Column(Float)
    predicted_score = Column(Float)

    created_at = Column(
        DateTime,
        server_default=func.now()
    )