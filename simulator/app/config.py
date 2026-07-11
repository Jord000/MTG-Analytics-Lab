import os

from dotenv import load_dotenv

# Load variables from .env into the environment
load_dotenv()


class Config:
    GAME_API_URL = os.getenv("GAME_API_URL", "http://localhost:8080")
    POST_INTERVAL_SECONDS = int(
        os.getenv("POST_INTERVAL_SECONDS", "5")
    )
    LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")