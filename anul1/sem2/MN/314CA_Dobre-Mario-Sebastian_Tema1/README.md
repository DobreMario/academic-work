> **Author**: DOBRE Mario-Sebastian
> **Group**: 314CA

## Markov is coming 🔗
Markov chains are mathematical models that describe systems transitioning between states, where the probability of each transition depends only on the current state.

Available files:

- **parse_labyrinth.m**: Takes a text file containing the encoding of a labyrinth and converts it into an internal matrix.

- **get_adjacency_matrix.m**: Creates the adjacency matrix associated with the graph derived from the labyrinth.

- **get_link_matrix.m**: Constructs the link matrix starting from the labyrinth. This matrix is efficiently stored for optimization.

- **get_Jacobi_parameters.m**: Takes the link matrix and extracts the parameters needed for applying the Jacobi method.

- **perform_iterative.m**: Solves Markov-type problems or linear systems using iterative methods, with a special focus on the Jacobi method.

- **heuristic_greedy.m**: Finds a winning path through the labyrinth using a greedy algorithm, which guarantees the solution due to the connectivity of the associated graphs.

- **decode_path.m**: Decodes the most probable sequence of states based on observations, using dynamic programming (e.g., the Viterbi algorithm).

**Additional reference**: Educational material - [Special Mathematics Course - UPB](https://curs.upb.ro/2024/pluginfile.php/298159/mod_resource/content/0/curs-MSp2025.pdf#section.2.6).

---

## Linear Regression 📈
Linear regression is a supervised learning algorithm used to model the relationship between a dependent variable and one or more independent variables.

Available files:

- **parse_csv_file.m**: Reads and parses CSV files to extract attributes and target values.

- **parse_data_set_file.m**: Processes other data files.

- **prepare_for_regression.m**: Normalizes attributes and transforms the received dataset according to the task requirements, splitting it into training and testing sets.

- **linear_regression_cost_function.m**: Computes the cost function for simple linear regression using the formula:

  $$
  J(\theta) = \frac{1}{2m} \sum_{i=1}^{m} \left( \left( \sum_{j=1}^{n} \theta_j x_j^{(i)} \right) - y^{(i)} \right)^2
  $$

  In this implementation, we used operation vectorization, inspired by the Gram-Schmidt method (lab 'y'), where instead of manual calculations for each element, a matrix approach is preferred, which handles all operations simultaneously and efficiently.

- **gradient_descent.m**: Implements the gradient descent algorithm, which updates the parameters as follows:

  $$
  \theta_j := \theta_j - \alpha \frac{\partial}{\partial \theta_j} J(\theta)
  $$

  where $\alpha$ is the learning rate, and $J(\theta)$ is the cost function.

- **normal_equation.m**: Provides a direct solution for linear regression by solving the normal equation:

  $$
  \theta = (X^T X)^{-1} X^T y
  $$

  Additionally, this function continues with solving the system of equations using the conjugate gradient method, but only if the matrix is positive definite. To check this condition, we used the `chol` function, which can decompose a matrix \( A = L L^T \). The symmetry of the matrix is ensured by the fact that \( A = X^T X \), and `chol` will fail only if the matrix is not positive definite. Official documentation for the `chol` function can be found [here](https://docs.octave.org/v4.4.0/Matrix-Factorizations.html?utm_source=chatgpt.com#XREFchol).

- **lasso_regression_cost_function.m**: Adds L1 regularization to the cost function:

  $$
  J(\theta) = \frac{1}{2m} \sum_{i=1}^m (h_\theta(x^{(i)}) - y^{(i)})^2 + \lambda \sum_{j=1}^n |\theta_j|
  $$

- **ridge_regression_cost_function.m**: Adds L2 regularization to the cost function:

  $$
  J(\theta) = \frac{1}{2m} \sum_{i=1}^m (h_\theta(x^{(i)}) - y^{(i)})^2 + \lambda \sum_{j=1}^n \theta_j^2
  $$

**Note**: For solving all tasks, we used the same approach applied in the `linear_regression_cost_function.m` function.

---

## MNIST 101 🖼️
MNIST is a standard dataset in machine learning, containing black-and-white 28×28 pixel images of handwritten digits, used for training and testing classification algorithms. In our case, the dataset is modified so that the images have a size of 20×20 pixels.

Available files:

- **load_dataset.m**: Loads the MNIST binary files and extracts the training and testing data (images and labels).

- **split_dataset.m**: Splits the dataset into a training subset and a testing subset, based on the desired percentage.

- **initialize_weights.m**: This function initializes a weight matrix for the neural network with scaled random values, using a method that helps improve the convergence of training algorithms. The weight values are chosen to prevent symmetry problems and ensure that neurons are not initialized to the same values.
  $$
  e = \frac{\sqrt{6}}{\sqrt{L_{\text{prev}} + L_{\text{next}}}}
  $$

- **cost_function.m**:

This function computes the cost function and gradients for a neural network with one hidden layer.

Processing structure:

### TODO1: Reshape parameters
- The vectorized parameters are rearranged into two matrices: **Theta1** and **Theta2**, which contain the weights between layers.
- **Theta1**: weights between the input layer and the hidden layer.
- **Theta2**: weights between the hidden layer and the output layer.

### TODO2: Forward propagation
- A bias is added to the input and hidden layer.
- The activations of each layer are computed using the sigmoid function:
- Adding bias to input data:

  - $$ 
    a^{(1)} = \begin{bmatrix} 1 \\ x \end{bmatrix}
    $$

- Computing the hidden layer activation:

  - $$
    z^{(2)} = \Theta^{(1)} a^{(1)}
    $$

  - $$
    a^{(2)} = \text{sigmoid}(z^{(2)})
    $$

- Adding bias to the hidden layer:

  - $$
    a^{(2)} = \begin{bmatrix} 1 \\ a^{(2)} \end{bmatrix}
    $$

- Computing the output layer activation:

  - $$
    z^{(3)} = \Theta^{(2)} a^{(2)}
    $$

  - $$
    a^{(3)} = \text{sigmoid}(z^{(3)})
    $$
- The final output ($a^{(3)}$) represents the network's prediction for each class.

Note: In this step of the neural network, for each training example \( x^{(i)} \), a bias term (value 1) must be added to the input vector. This is done by adding a column of 1s to the input matrix \( X \), so that each input vector has a bias term at the beginning. 

Code:

```matlab
a1 = [ones(m, 1) X];  % Add a column of 1s to matrix X
a1 = a1';             % Transpose the matrix to get the correct shape
```

- **Adding a bias term**: In neural networks, a bias term is added to each unit of the input layer to allow the network to better adjust to the training data. In this case, we add a column of 1s to the matrix \( X \). This is done to introduce the bias term in each step of the network's computation.

- **Final shape of \( a^{(1)} \)**: After adding a column of 1s to the matrix \( X \), we transpose the result to get the correct shape of the input vector \( a^{(1)} \). Thus, for each training example, we will have the input vector:

  $$
  a^{(1)} = \begin{bmatrix} 1 \\ x \end{bmatrix}
  $$

  where \( x \) is the vector of pixel values for each image, and 1 is the bias term. This allows the neural network to better learn the offsets (bias) associated with the activations of each neuron.

This is the first step in forward propagation and is repeated for each layer of the neural network.



### TODO3: Backpropagation
- Compute the errors (deltas) for the output layer and the hidden layer:
  - $$\delta^{(3)} = a^{(3)} - y_{\text{OHE}}$$
  - $$\delta^{(2)} = (\Theta^{(2)})^T \delta^{(3)}.*\text{sigmoid}'(z^{(2)})$$
- Accumulate the total gradients: **Delta1**, **Delta2**.

### TODO4: Compute gradients
- Gradients for each layer are obtained as the average of the Deltas:
  - $$
    \Theta^{(l)}_{i, j} =
    \begin{cases}
    \frac{1}{m} \Delta^{(l)}_{i, j}, & \text{dacă } j = 0 \quad (\text{bias}) \\\\
    \frac{1}{m} \Delta^{(l)}_{i, j} + \frac{\lambda}{m} \Theta^{(l)}_{i, j}, & \text{dacă } j > 0
    \end{cases}
    $$

### TODO5: Compute the cost function
- The cost function is the average error for all examples:
  - $$
    J(\theta) = -\frac{1}{m} \sum_{i=1}^m \left[ y^{(i)} \log(a^{(3)}) + (1 - y^{(i)}) \log(1 - a^{(3)}) \right]
    $$
- Add the regularization term:
  - $$
    \text{reg} = \frac{\lambda}{2m} \left( \sum \Theta1^2 + \sum \Theta2^2 \right)
    $$
- The final cost is the sum of the average error and regularization:
  - $$
    J_{\text{final}} = J(\theta) + \text{reg}
    $$

Note: One-Hot Encoding transforms numeric labels into a binary representation, compatible with the neural network's output.

- **predict_classes.m**: This section extracts the weights Theta_1 and Theta_2 from the `weights` vector, adds the bias term to the input matrix, and computes activations through forward propagation. Finally, the predicted class is obtained by selecting the maximum value from the network's output, similar to the steps in the previous function. **(TODO2)**

---

