import json
import logging
import random
import time

import requests

from app.config import Config
from app.game_entry_request import GameEntryRequest
from pathlib import Path

logging.basicConfig(level=Config.LOG_LEVEL)
logger = logging.getLogger(__name__)

BASE_DIR = Path(__file__).resolve().parent


def load_json(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def choose_commander(commanders):
    return random.choices(
        commanders,
        weights=[c["weight"] for c in commanders],
        k=1,
    )[0]


def generate_game(players, commanders):
    """
    Generates a single Commander game consisting of four player results.
    Exactly one player wins.
    """

    game_players = random.sample(players, 4)
    winner = random.choice(game_players)

    # All players in the same game share the same turn count
    turns = max(5, min(20, int(random.gauss(10, 2))))

    entries = []

    for player in game_players:
        commander = choose_commander(commanders)

        entries.append(
            GameEntryRequest(
                player=player,
                commander=commander["name"],
                colorIdentity=commander["colourIdentity"],
                win=(player == winner),
                numberOfTurnsPlayed=turns,
            )
        )

    return entries


def post_game_entry(entry: GameEntryRequest):
    url = f"{Config.GAME_API_URL}/game-entry"

    try:
        response = requests.post(
            url,
            json=entry.to_dict(),
            timeout=5,
        )

        response.raise_for_status()

        logger.info(
            "%s | %s | win=%s | turns=%d",
            entry.player,
            entry.commander,
            entry.win,
            entry.numberOfTurnsPlayed,
        )

    except requests.RequestException as e:
        logger.error("Failed to post game entry: %s", e)


def run_simulator(players, commanders):
    interval = Config.POST_INTERVAL_SECONDS

    logger.info("Starting simulator...")
    logger.info("Posting one 4-player game every %s seconds", interval)

    while True:
        entries = generate_game(players, commanders)

        for entry in entries:
            post_game_entry(entry)

        time.sleep(interval)


def main():
    players = load_json(BASE_DIR / "data" / "players.json")
    commanders = load_json(BASE_DIR / "data" / "commanders.json")

    if len(players) < 1:
        raise ValueError("players.json must contain at least one player.")
    if len(commanders) < 1:
        raise ValueError("commanders.json must contain at least one commander.")

    run_simulator(players, commanders)


if __name__ == "__main__":
    main()
