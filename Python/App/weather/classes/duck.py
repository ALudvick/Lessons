class Duck:
    # private variable
    _score = 0

    # constructor
    def __init__(self, nickname, age, position):
        self._nickname = nickname
        self._age = age
        self._position = position

    # simple method
    def say_hello(self):
        if self._position is True:
            print(f'Привет, утка {self._nickname}! Твой возраст - {self._age}, ты танк!')
        else:
            print(f'Привет, утка {self._nickname}! Твой возраст - {self._age}, ты хиллер и всегда виноват!')

    # setter
    def set_score(self, word):
        self._score += len(word)

    # getter
    def get_score(self):
        return self._score

    # getter
    def get_nickname(self):
        return self._nickname

    # override toString() method
    def __str__(self) -> str:
        return 'Duck{nickname=\'' + self._nickname + '\', age=\'' + str(self._age) + '\', position=\'' + str(self._position) + '\', score=\'' + str(self._score) + '\'}'


