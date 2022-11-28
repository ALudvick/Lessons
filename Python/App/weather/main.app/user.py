class User:
    _user_id = None
    _user_name = None
    _user_role = None
    _message = None
    _message_id = None

    def __init__(self, user_id, user_name, user_role):
        self._user_id = user_id
        self._user_name = user_name
        self._user_role = user_role

    @property
    def user_id(self):
        return self._user_id

    @property
    def user_name(self):
        return self._user_name

    @property
    def user_role(self):
        return self._user_role

    @property
    def message(self):
        return self._message

    @message.setter
    def message(self, value):
        self._message = value

    @property
    def query_id(self):
        return self._message_id

    @query_id.setter
    def query_id(self, value):
        self._message_id = value

    def __eq__(self, other):
        return (self.__class__ == other.__class__
                and self._user_id == other.user_id
                and self._user_name == other.user_name)

    def __str__(self) -> str:
        return f'{self._user_id}: {self._user_name} ({self._user_role}). {self._message_id}: {self._message}'


