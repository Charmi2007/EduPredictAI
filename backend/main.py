from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import bcrypt
import pandas as pd

from database import SessionLocal
from models import AcademicData, Student

# Create FastAPI application
app = FastAPI(
    title="EduPredict AI API",
    description="AI-powered academic performance prediction API",
    version="1.0"
)


# Load trained ML model
model = joblib.load(
    "model/student_performance_model.pkl"
)


# Student input model
class StudentData(BaseModel):
    student_id: int
    previous_score: float
    study_hours: float
    quiz_average: float
    quizzes_attempted: int
    previous_exam_score: float
    learning_activity: float

class RegisterData(BaseModel):
    name: str
    email: str
    password: str


class LoginData(BaseModel):
    email: str
    password: str

@app.post("/register")
def register(data: RegisterData):

    db = SessionLocal()

    try:
        existing_student = (
            db.query(Student)
            .filter(Student.email == data.email)
            .first()
        )

        if existing_student:
            return {
                "success": False,
                "message": "Email already registered"
            }

        hashed_password = bcrypt.hashpw(
            data.password.encode("utf-8"),
            bcrypt.gensalt()
        ).decode("utf-8")

        student = Student(
            name=data.name,
            email=data.email,
            password=hashed_password
        )

        db.add(student)
        db.commit()
        db.refresh(student)

        return {
            "success": True,
            "message": "Registration successful!",
            "student_id": student.id,
            "name": student.name,
            "email": student.email
        }

    finally:
        db.close()

@app.post("/login")
def login(data: LoginData):

    db = SessionLocal()

    try:
        student = (
            db.query(Student)
            .filter(Student.email == data.email)
            .first()
        )

        if not student:
            return {
                "success": False,
                "message": "Invalid email or password"
            }

        password_match = bcrypt.checkpw(
            data.password.encode("utf-8"),
            student.password.encode("utf-8")
        )

        if not password_match:
            return {
                "success": False,
                "message": "Invalid email or password"
            }

        return {
            "success": True,
            "message": "Login successful!",
            "student_id": student.id,
            "name": student.name,
            "email": student.email
        }

    finally:
        db.close()


# Home endpoint
@app.get("/")
def home():
    return {
        "message": "EduPredict AI API is running"
    }


# Prediction endpoint
@app.post("/predict")
def predict(data: StudentData):

    # Prepare input for ML model using feature names
    input_df = pd.DataFrame([{
        "previous_score": data.previous_score,
        "study_hours": data.study_hours,
        "quiz_average": data.quiz_average,
        "quizzes_attempted": data.quizzes_attempted,
        "previous_exam_score": data.previous_exam_score,
        "learning_activity": data.learning_activity
    }])

    # ML prediction
    prediction = model.predict(input_df)[0]

    predicted_score = round(
        float(prediction),
        2
    )

    # Identify learning gaps
    weak_areas = []
    recommendations = []

    if data.quiz_average < 70:
        weak_areas.append("Quiz Performance")
        recommendations.append(
            "Practice quizzes for 30 minutes every day"
        )

    if data.previous_exam_score < 70:
        weak_areas.append("Exam Performance")
        recommendations.append(
            "Review topics from your previous exam"
        )

    if data.study_hours < 3:
        weak_areas.append("Study Routine")
        recommendations.append(
            "Increase your daily study time"
        )

    if data.learning_activity < 70:
        weak_areas.append("Learning Activity")
        recommendations.append(
            "Practice more learning activities"
        )

    if not weak_areas:
        weak_areas.append(
            "No major weakness detected"
        )

        recommendations.append(
            "Continue your current learning routine"
        )

    # Daily study goal
    if predicted_score >= 85:
        daily_goal = "2 Hours / Day"
    elif predicted_score >= 70:
        daily_goal = "2.5 Hours / Day"
    else:
        daily_goal = "3 Hours / Day"

    # -------------------------------
    # Save prediction to MySQL
    # -------------------------------

    db = SessionLocal()

    try:

        academic_record = AcademicData(
            student_id=data.student_id,
            previous_score=data.previous_score,
            study_hours=data.study_hours,
            quiz_average=data.quiz_average,
            quizzes_attempted=data.quizzes_attempted,
            previous_exam_score=data.previous_exam_score,
            learning_activity=data.learning_activity,
            predicted_score=predicted_score
        )

        db.add(academic_record)
        db.commit()
        db.refresh(academic_record)

    finally:

        db.close()

    # Return prediction
    return {
        "predicted_score": predicted_score,
        "weak_areas": weak_areas,
        "recommendations": recommendations,
        "daily_goal": daily_goal
    }

# ---------------------------------------
# Get latest academic data for a student
# ---------------------------------------
@app.get("/academic/{student_id}")
def get_latest_academic_data(student_id: int):

    db = SessionLocal()

    try:
        academic_record = (
            db.query(AcademicData)
            .filter(AcademicData.student_id == student_id)
            .order_by(AcademicData.created_at.desc())
            .first()
        )

        if not academic_record:
            return {
                "success": False,
                "message": "No academic data found"
            }

        return {
            "success": True,
            "student_id": academic_record.student_id,
            "previous_score": academic_record.previous_score,
            "study_hours": academic_record.study_hours,
            "quiz_average": academic_record.quiz_average,
            "quizzes_attempted": academic_record.quizzes_attempted,
            "previous_exam_score": academic_record.previous_exam_score,
            "learning_activity": academic_record.learning_activity,
            "predicted_score": academic_record.predicted_score,
            "created_at": academic_record.created_at
        }

    finally:
        db.close()

# ---------------------------------------
# Get prediction history for a student
# ---------------------------------------
@app.get("/academic/history/{student_id}")
def get_prediction_history(student_id: int):

    db = SessionLocal()

    try:
        academic_records = (
            db.query(AcademicData)
            .filter(AcademicData.student_id == student_id)
            .order_by(AcademicData.created_at.desc())
            .all()
        )

        if not academic_records:
            return {
                "success": False,
                "message": "No prediction history found",
                "history": []
            }

        history = []

        for record in academic_records:
            history.append({
                "id": record.id,
                "previous_score": record.previous_score,
                "study_hours": record.study_hours,
                "quiz_average": record.quiz_average,
                "quizzes_attempted": record.quizzes_attempted,
                "previous_exam_score": record.previous_exam_score,
                "learning_activity": record.learning_activity,
                "predicted_score": record.predicted_score,
                "created_at": record.created_at
            })

        return {
            "success": True,
            "message": "Prediction history retrieved successfully",
            "history": history
        }

    finally:
        db.close()