import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix, classification_report

# 1. Tipul problemei
print("Problema abordată: CLASIFICARE binară (depressed: 0/1)")

# 2. Încărcare și structură dataset
os.makedirs('plots', exist_ok=True)
os.makedirs('datasets/train', exist_ok=True)
os.makedirs('datasets/test', exist_ok=True)

depression = pd.read_csv('datasets/b_depressed.csv')
print(f"Setul de date are {depression.shape[0]} instanțe și {depression.shape[1]} coloane.")

# 3. Analiza valorilor lipsă
print("\nAnaliza valorilor lipsă:")
for col in depression.columns:
    n_missing = depression[col].isna().sum()
    pct_missing = n_missing / len(depression) * 100
    print(f"{col}: {n_missing} missing ({pct_missing:.2f}%)")

# Imputare pentru 'no_lasting_investmen'
mean_value = depression['no_lasting_investmen'].mean()
depression['no_lasting_investmen'] = depression['no_lasting_investmen'].fillna(mean_value)

# 4. Statistici descriptive
print("\nStatistici descriptive:")
print(depression.describe(include='all'))

# 5. Analiza distribuției variabilelor
numeric_cols = ['Age', 'Number_children', 'education_level', 'total_members',
                'gained_asset', 'durable_asset', 'save_asset', 'living_expenses',
                'other_expenses', 'incoming_agricultural', 'farm_expenses',
                'lasting_investment', 'no_lasting_investmen']
categorical_cols = ['sex', 'Married', 'incoming_no_business', 'incoming_salary', 'incoming_own_farm', 'incoming_business', 'labor_primary', 'depressed']

# Histograme
plt.figure(figsize=(15, 20))
for i, col in enumerate(numeric_cols):
    plt.subplot(5, 3, i+1)
    sns.histplot(data=depression, x=col, kde=True, bins=30)
    plt.title(f'Distribuția: {col}')
plt.tight_layout()
plt.savefig('plots/histograme_variabile_numerice.png')
plt.close()

# Countplot categorice
plt.figure(figsize=(20, 15))
for i, col in enumerate(categorical_cols, 1):
    plt.subplot(4, 2, i)
    sns.countplot(data=depression, x=col)
    plt.title(f'Countplot: {col}')
plt.tight_layout()
plt.savefig('plots/countplot_variabile_categorice.png')
plt.close()

# Boxplot outlieri
plt.figure(figsize=(8, 6))
sns.boxplot(x=depression['Age'])
plt.title('Boxplot pentru variabila Age')
plt.xlabel('Age')
plt.tight_layout()
plt.savefig('plots/boxplot_age.png')
plt.close()

# Violin plot Age vs depressed
plt.figure(figsize=(8, 6))
sns.violinplot(x='depressed', y='Age', data=depression, hue='depressed', palette='Set2', legend=False)
plt.title('Distribuția vârstei în funcție de depresie')
plt.xlabel('Depressed')
plt.ylabel('Age')
plt.tight_layout()
plt.savefig('plots/violinplot_age_vs_depressed.png')
plt.close()

# 6. Matrice de corelații
plt.figure(figsize=(12,10))
corr = depression[numeric_cols].corr()
sns.heatmap(corr, annot=True, cmap='coolwarm', fmt=".2f")
plt.title("Matricea de corelații - variabile numerice")
plt.tight_layout()
plt.savefig('plots/correlation_matrix.png')
plt.close()

# 7. Împărțire train/test și salvare
X = depression.drop(columns=['depressed', 'Survey_id', 'Ville_id'])
y = depression['depressed']
smote = SMOTE(sampling_strategy='minority', random_state=42)
X_resampled, y_resampled = smote.fit_resample(X, y)
X_train, X_test, y_train, y_test = train_test_split(X_resampled, y_resampled, test_size=0.2, random_state=42)

print(f"\nTrain: {X_train.shape[0]} instanțe, Test: {X_test.shape[0]} instanțe")
X_train.to_csv('datasets/train/train_features.csv', index=False)
y_train.to_csv('datasets/train/train_target.csv', index=False)
X_test.to_csv('datasets/test/test_features.csv', index=False)
y_test.to_csv('datasets/test/test_target.csv', index=False)

# 8. Modelare și evaluare

# Analiză importanță variabile (feature importance)
model = ExtraTreesClassifier(n_estimators=100, random_state=42, class_weight='balanced')
model.fit(X_train, y_train)

importances = model.feature_importances_
feature_names = X_train.columns
feat_imp = sorted(zip(feature_names, importances), key=lambda x: x[1], reverse=True)
print("\nImportanța variabilelor (feature importance):")
for name, imp in feat_imp:
    print(f"{name}: {imp:.4f}")

# Selectează top 10 variabile cele mai importante
top10_features = [name for name, imp in feat_imp[:10]]

# Reantrenează și evaluează modelul doar cu top 10 variabile

model_top10 = ExtraTreesClassifier(n_estimators=500, random_state=42, class_weight='balanced')
model_top10.fit(X_train[top10_features], y_train)
y_pred_top10 = model_top10.predict(X_test[top10_features])

print("\nRezultate model (doar top 10 variabile):")
print(f"Acuratețe: {accuracy_score(y_test, y_pred_top10):.2f}")
print(f"Precizie: {precision_score(y_test, y_pred_top10):.2f}")
print(f"Recall: {recall_score(y_test, y_pred_top10):.2f}")
print(f"F1-score: {f1_score(y_test, y_pred_top10):.2f}")
print("\nRaport clasificare:")
print(classification_report(y_test, y_pred_top10))

# Matrice de confuzie pentru top 10 variabile
cm_top10 = confusion_matrix(y_test, y_pred_top10)
plt.figure(figsize=(6,5))
sns.heatmap(cm_top10, annot=True, fmt='d', cmap='Blues', xticklabels=[0,1], yticklabels=[0,1])
plt.xlabel('Predicted')
plt.ylabel('Actual')
plt.title('Matrice de confuzie (top 10 variabile)')
plt.tight_layout()
plt.savefig('plots/confusion_matrix_top10.png')
plt.close()

