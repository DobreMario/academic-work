function coef = vandermonde(x, y)
    % x = [x0, x1, ..., xn]'
    % y = [y0, y1, ..., yn]'
    % coef = [a0, a1, a2, ..., an]'

    % TODO: Calculate the Vandermonde coefficients

    % Computes the coefficients of the interpolating polynomial using the Lagrange method.
    % For each point (x_i, y_i), it builds the Lagrange basis L_i(x) and forms:
    %   P(x) = sum y_i * L_i(x)
    %
    % Note: This method is intuitive but numerically unstable for many points
    % or closely spaced points. For better stability, methods like solving the
    % Vandermonde system or Newton interpolation are recommended.

    n = length(x);
    coef = zeros(1, n);
    pol = zeros(1, n);
    for i = 1 : n
        L = 1;
        num = 1;
        for j = 1 : n
            if j ~= i
                % Calculate the Lagrange basis polynomial
                L = conv(L, [1, -x(j)]);
                num = num * (x(i) - x(j));
            end
        end
        pol = pol + y(i) * L / num;
    end
    % The coefficients are in reverse order
    coef = fliplr(pol);
endfunction
