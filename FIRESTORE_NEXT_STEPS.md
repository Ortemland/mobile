# ⚡ После создания Firestore

## Сразу после нажатия "Создавать":
1. Выбери регион (например: **Belgium** или **Europe**)
2. Нажми **"Создавать"** (Enable)

## После создания базы:

### Вкладка "Правила" (Rules)

Найди вкладку **"Правила"** вверху и замени код на:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /families/{familyId} {
      allow read, write: if true;
      
      match /pendingApprovals/{taskId} {
        allow read, write: if true;
      }
    }
  }
}
```

Нажми **"Опубликовать"** (Publish)

## ✅ Готово!
Теперь можно запускать приложение и оно будет работать с Firebase!

