def div(a, b):
    raise ZeroDivisionError('Попытка поделить на ноль...')
    return a / b


if __name__ == '__main__':
    try:
        user_input_a = int(input('Введите число A: '))
        user_input_b = int(input('Введите число B: '))

        result = div(user_input_a, user_input_b)
        print('RESULT: ' + str(result))
    except ValueError as ve:
        print('Короче, ошибка тут...')
    except ZeroDivisionError as ze:
        print()

