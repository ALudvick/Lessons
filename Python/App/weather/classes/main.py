import duck

players = []


def create_player():
    print('Создается игрок...')
    nickname = input('Введите имя игрока: ')
    age = int(input('Введите возраст: '))

    user_position = str(input('Ты танк или нет (y/n): '))
    if user_position.lower() == 'y':
        position = True
    elif user_position.lower() == 'n':
        position = False
    else:
        print('Я так и не понял, кто ты, поэтому ты хил...')
        position = False

    print('Персонаж создан!')
    return duck.Duck(nickname, age, position)


def create_players_list(players_count):
    for i in range(0, players_count):
        players.append(create_player())

    print('\nВсе персонажи созданы:')
    for pl in players:
        print(pl)


def start_game():
    is_active = True
    print('\nИгра началась')

    while is_active:
        user_input = input('Введите номер игрока: ')
        if user_input.lower() == 'exit':
            is_active = False
        else:
            number = int(user_input)
            if 0 < number <= len(players):
                players[number - 1].set_score(input('Введите слово: '))

    print('\nРезультаты: ')
    for player in players:
        print(f'Очков у утки {player.get_nickname()}: {player.get_score()}')


if __name__ == '__main__':
    number_of_players = int(input('Введите количество игроков: '))
    create_players_list(number_of_players)
    start_game()
