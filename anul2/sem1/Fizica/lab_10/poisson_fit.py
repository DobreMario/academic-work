import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import poisson

# ----------------------------
#  Introdu date proprii aici
# ----------------------------
# Exemplu:
# data = np.array([5, 7, 6, 8, 7, 6, 10, 5, 7])

data = np.array([ ... ])  # <-- introdu datele tale aici (numere întregi)

# ----------------------------
#  Estimare λ (MLE)
# ----------------------------
lambda_fit = np.mean(data)
print("Lambda fit =", lambda_fit)

# ----------------------------
#  Histogramă + funcție Poisson
# ----------------------------
max_k = np.max(data) + 3  # histograma acoperă zona relevantă
counts, bins = np.histogram(data, bins=range(0, max_k))
x = np.arange(0, len(counts))

# Poisson teoretică scalată cu numărul de evenimente
poisson_fit = poisson.pmf(x, lambda_fit) * len(data)

plt.figure(figsize=(8,5))
plt.bar(x, counts, alpha=0.6, label="Date experimentale")
plt.plot(x, poisson_fit, 'o-', label="Poisson fit", linewidth=2)

plt.xlabel("k")
plt.ylabel("Număr de evenimente")
plt.title("Fit Poisson")
plt.legend()

# ----------------------------
#  Salvare grafic
# ----------------------------
plt.savefig("fit_poisson.png", dpi=300, bbox_inches='tight')
plt.close()

print("Plot salvat ca: fit_poisson.png")
