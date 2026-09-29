import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

# -------------------------------
# 1️⃣ Funcția Gaussiană
# -------------------------------
def gaussian(x, A, mu, sigma, offset):
    """Funcție Gaussiană simplă cu offset"""
    return A * np.exp(-(x - mu)**2 / (2 * sigma**2)) + offset

# -------------------------------
# 2️⃣ Citirea datelor din fișiere
# -------------------------------
# Fisierele trebuie să aibă 2 coloane:
#   col0 = număr canal
#   col1 = intensitate / conturi

background = np.loadtxt("bg.txt")
natriu = np.loadtxt("Na.txt")

# Verificăm dacă lungimile coincid
if background.shape[0] != natriu.shape[0]:
    raise ValueError("Fișierele au lungimi diferite! Trebuie să aibă același număr de canale.")

# -------------------------------
# 3️⃣ Corectarea spectrului
# -------------------------------
# Se scade semnalul de background din cel al sodiului
corectat = natriu[:, 1] - background[:, 1]

# -------------------------------
# 4️⃣ Fit Gaussian pe spectrul corectat
# -------------------------------
xdata = natriu[:, 0]
ydata = corectat

# Estimare parametri inițiali: [amplitudine, centru, sigma, offset]
p0 = [np.max(ydata), xdata[np.argmax(ydata)], 20, np.min(ydata)]

# Fit Gaussian
popt, pcov = curve_fit(gaussian, xdata, ydata, p0=p0)

A, mu, sigma, offset = popt
print(f"Rezultatele fitului:")
print(f"  Amplitudine = {A:.3f}")
print(f"  Centru (canal) = {mu:.3f}")
print(f"  Sigma = {sigma:.3f}")
print(f"  Offset = {offset:.3f}")

# -------------------------------
# 5️⃣ Vizualizare și salvare grafic
# -------------------------------

plt.figure(figsize=(14, 8), dpi=150)  # grafic mai mare și rezoluție mai bună

# Graficul spectrului corectat
plt.plot(xdata, ydata, label="Spectru corectat", lw=2, color='blue')

# Graficul fitului Gaussian
plt.plot(xdata, gaussian(xdata, *popt), 'r--', lw=3, label="Fit Gaussian")

# Etichete și titlu clar, fonturi mari
plt.xlabel("Număr canal", fontsize=14)
plt.ylabel("Conturi corectate", fontsize=14)
plt.title("Fit Gaussian pe spectrul de Na corectat", fontsize=16, weight='bold')

# Aspect vizual mai clar
plt.legend(fontsize=12)
plt.grid(True, linestyle='--', alpha=0.6)
plt.tight_layout()

# Salvare imagine
plt.savefig("fit_gaussian_natriu.png", dpi=300, bbox_inches='tight')
print("✅ Graficul a fost salvat ca 'fit_gaussian_natriu.png' în folderul curent.")

# Afișare pe ecran
plt.show()


# -------------------------------
# 6️⃣ Salvare rezultate
# -------------------------------
rezultate = np.column_stack((xdata, corectat))
np.savetxt("rezultate_corectate.txt", corectat, fmt="%.6f", header="Corectat", comments='')

print("\nFișierul 'rezultate_corectate.txt' a fost salvat cu succes.")
