import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

# ----------------------------
#  Functia Gaussiana
# ----------------------------
def gauss(x, A, mu, sigma):
    return A * np.exp(-(x - mu)**2 / (2 * sigma**2))

# ----------------------------
#  Introdu date proprii aici
# ----------------------------
# Exemplu:
# x_data = np.array([0, 1, 2, 3, 4, 5])
# y_data = np.array([1, 4, 9, 5, 3, 2])

x_data = np.array([ ... ])  # <-- Introdu valorile tale
y_data = np.array([ ... ])  # <-- Introdu valorile tale

# ----------------------------
#  Fit Gaussian
# ----------------------------
popt, pcov = curve_fit(gauss, x_data, y_data, p0=[max(y_data), x_data[np.argmax(y_data)], 1])
A_fit, mu_fit, sigma_fit = popt

print("Rezultate fit:")
print("A =", A_fit)
print("mu =", mu_fit)
print("sigma =", sigma_fit)

# ----------------------------
#  Plot + Salvare imagine
# ----------------------------
plt.figure(figsize=(8,5))
plt.scatter(x_data, y_data, label="Date introduse")
plt.plot(x_data, gauss(x_data, *popt), label="Gaussian fit", linewidth=2)

plt.legend()
plt.xlabel("x")
plt.ylabel("y")
plt.title("Fit Gaussian")

plt.savefig("fit_gaussian.png", dpi=300, bbox_inches='tight')
plt.close()

print("Plot salvat ca: fit_gaussian.png")
