import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

# Configurable database URL (environment variable takes priority for production deployment)
DEFAULT_DB_URL = "mysql+pymysql://root:Gajula%4028@localhost:3306/edupredict"
DATABASE_URL = os.getenv("DATABASE_URL", DEFAULT_DB_URL)

engine = create_engine(
    DATABASE_URL,
    echo=False  # Set to False to prevent logging raw queries and passwords in production logs
)

SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

Base = declarative_base()