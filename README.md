# EduPredictAI 🎓

**AI-Powered Academic Performance Prediction and Personalized Learning Android App**

EduPredictAI is an Android application that uses machine learning to estimate students' academic performance based on their previous scores, study habits, quiz results, and learning activity. It helps students understand their progress, explore areas for improvement, and make more informed study decisions.

## ✨ Features

- **Academic Performance Prediction:** Estimate expected academic scores using a trained machine learning model.
- **Student Dashboard:** View academic information and access the app's main features.
- **Academic Data Management:** Enter relevant study and performance information.
- **Personalized Learning:** Access learning resources and study guidance.
- **Quiz Module:** Practice through quizzes and review learning progress.
- **Analytics:** Visualize academic performance and progress.
- **Prediction History:** Review previous predictions.
- **User Authentication:** Register, log in, and manage a profile.

## 🛠️ Tech Stack

| Component | Technologies |
|---|---|
| Android application | Java, XML |
| Development environment | Android Studio |
| Backend API | Python, FastAPI |
| Machine learning | Scikit-learn |
| Database | MySQL |
| Database integration | SQLAlchemy |
| API communication | Retrofit, Gson |
| Model storage | Joblib / Pickle-compatible model file |

## 🏗️ System Architecture

1. **Data Input:** The student enters academic and learning-related information.
2. **Android Application:** The app sends requests to the backend API.
3. **FastAPI Backend:** Receives requests and processes the submitted information.
4. **Machine Learning Model:** Generates an estimated academic performance prediction.
5. **Database:** Stores and retrieves supported application data.
6. **Results and Analytics:** The app displays predictions, history, and academic progress.

## 📂 Project Structure

```text
EduPredictAI/
├── app/
│   └── src/main/
│       ├── java/com/example/edupredictai/
│       └── res/
├── backend/
│   ├── data/
│   │   └── student_performance.csv
│   ├── model/
│   │   └── student_performance_model.pkl
│   ├── main.py
│   ├── database.py
│   ├── models.py
│   ├── train.py
│   └── test_database.py
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Android Studio and a compatible Android SDK
- Python installed on your system
- MySQL Server
- A physical Android device or Android emulator

### 1. Clone the repository

```bash
git clone https://github.com/Charmi2007/EduPredictAI.git
cd EduPredictAI
```

### 2. Set up the backend

Open a terminal in the backend directory:

```bash
cd backend
python -m venv .venv
```

Activate the virtual environment on Windows:

```powershell
.\.venv\Scripts\Activate.ps1
```

Install the required Python packages. Create a `requirements.txt` containing the backend dependencies if one is not already available, then run:

```bash
pip install -r requirements.txt
```

Configure the MySQL database and connection settings according to `database.py`. Do not commit database passwords or other secrets.

### 3. Start the API

From the backend directory, run:

```bash
python -m uvicorn main:app --host 0.0.0.0 --port 8000
```

Open the interactive API documentation at:

`http://127.0.0.1:8000/docs`

### 4. Run the Android app

1. Open the project in Android Studio.
2. Allow Gradle synchronization to complete.
3. Configure the backend URL in the app's server connection settings.
4. Ensure the Android device can reach the backend.
5. Run the application on an emulator or connected Android device.

For a physical device, use the computer's reachable local IP address instead of `127.0.0.1`. The computer and phone generally need to be on a network that permits communication between them.

## 📱 Download

An installable Android APK can be published in the repository's **Releases** section once a build has been generated and tested.

## 🔐 Security Notes

- Never commit database passwords, API secrets, or personal credentials.
- Use secure configuration for database connections.
- A locally running backend is not automatically accessible over the public internet.

## 🎯 Project Objective

The goal of EduPredictAI is to combine mobile technology, machine learning, and academic analytics to help students monitor their learning progress and make data-informed study decisions.

## 👩‍💻 Author

**Charmi**

GitHub: [@Charmi2007](https://github.com/Charmi2007)

## 📄 License

A license can be added if you decide how you want others to use, modify, and distribute this project.
