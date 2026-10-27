import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, r2_score
import joblib
import os

# Load dataset
data_path = "data/student_performance.csv"
df = pd.read_csv(data_path)

# Input features
X = df[
    [
        "previous_score",
        "study_hours",
        "quiz_average",
        "quizzes_attempted",
        "previous_exam_score",
        "learning_activity"
    ]
]

# Target
y = df["final_score"]

# Split data
X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42
)

# Create model
model = RandomForestRegressor(
    n_estimators=100,
    random_state=42
)

# Train model
model.fit(X_train, y_train)

# Predictions
predictions = model.predict(X_test)

# Evaluation
mae = mean_absolute_error(y_test, predictions)
r2 = r2_score(y_test, predictions)

print("Model Training Completed")
print("MAE:", mae)
print("R2 Score:", r2)

# Create model directory if it doesn't exist
os.makedirs("model", exist_ok=True)

# Save trained model
model_path = "model/student_performance_model.pkl"
joblib.dump(model, model_path)

print("Model saved successfully.")
print("Model path:", model_path)