from dataclasses import dataclass, asdict


@dataclass
class GameEntryRequest:
    player: str
    commander: str
    colorIdentity: str
    win: bool
    numberOfTurnsPlayed: int

    def to_dict(self) -> dict:
        """
        Convert the GameEntry into a dictionary ready for JSON serialization.
        """
        return asdict(self)