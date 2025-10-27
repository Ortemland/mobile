# 🔥 Настройка Firebase для приложения

## Шаг 1: Создать проект в Firebase Console

1. Открой [Firebase Console](https://console.firebase.google.com/)
2. Нажми **"Добавить проект"** или **"Create a project"**
3. Введи название проекта: **ScreenTime Reward**
4. Нажми **"Продолжить"** (можно отключить Google Analytics)
5. Нажми **"Создать проект"**

## Шаг 2: Добавить Android приложение

1. В Firebase Console открой созданный проект
2. Нажми на иконку **Android** (🟢) или **"Добавить приложение"** → **Android**
3. Заполни форму:
   - **Имя пакета Android**: `com.screentime.reward`
   - **Псевдоним приложения**: `ScreenTime Reward` (опционально)
   - **Сертификат SHA-1**: пока можно пропустить (нужен для авторизации)
4. Нажми **"Зарегистрировать приложение"**

## Шаг 3: Скачать google-services.json

1. После регистрации скачай файл `google-services.json`
2. **ВАЖНО!** Замени файл `app/google-services.json` в проекте скачанным файлом
3. Файл должен быть здесь: `/app/google-services.json`

## Шаг 4: Настроить Firestore Database

1. В Firebase Console перейди в **Firestore Database** (в меню слева)
2. Нажми **"Создать базу данных"**
3. Выбери **"Начать в тестовом режиме"** (для разработки)
4. Выбери регион (ближайший к тебе, например: `eur3`)
5. Нажми **"Включить"**

## Шаг 5: Настроить правила безопасности Firestore

1. Перейди в **"Правила"** в Firestore Database
2. Замени правила на:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // پیدا для семей
    match /families/{familyId} {
      // Чтение и запись разрешены всем (для разработки)
      allow read, write: if true;
      
      // Заявки на утверждение
      match /pendingApprovals/{taskId} {
        allow read, write: if true;
      }
    }
  }
}
```

3. Нажми **"Опубликовать"**

## Шаг 6: Настроить Firebase Authentication (опционально)

1. В Firebase Console перейди в **Authentication**
2. Нажми **"Начать"**
3. Можно использовать анонимную авторизацию или Email/Password

## Шаг 7: Синхронизировать проект

1. В Android Studio нажми **File → Sync Project with Gradle Files**
2. Дождись завершения синхронизации

## ✅ Проверка

После настройки убедись:
- Файл `app/google-services.json` заменен на скачанный
- Firebase BOM добавлен в `app/build.gradle.kts`
- Проект успешно синхронизирован без ошибок

## 📝 Структура данных в Firestore

После запуска приложения в Firestore будет создана такая структура:

```
families/
  └── {familyId}/
      ├── familyId: string
      ├── adultDeviceId: string
      ├── childDeviceId: string
      ├── connectionCode: string (6 цифр)
      ├── isActive: boolean
      └── pendingApprovals/
          └── {taskId}/
              ├── taskId: string
              ├── taskName: string
              ├── timeMinutes: number
              ├── timestamp: timestamp
              ├── childDeviceId: string
              └── status: string (pending/approved/rejected)
```

## 🚨 Важно для продакшена

В будущем нужно:
1. Изменить правила Firestore на более безопасные
2. Добавить реальную авторизацию пользователей
3. Настроить уведомления (Firebase Cloud Messaging)
4. Добавить хранилище для настроек базового времени
5. Реализовать удаление устаревших данных

