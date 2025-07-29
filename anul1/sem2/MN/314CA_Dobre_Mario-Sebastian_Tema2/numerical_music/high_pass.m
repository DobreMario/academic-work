function signal = high_pass(signal, fs, cutoff_freq)
  % Fourier Transform of the signal
  x = fft(signal);

  % Nr of samples
  E = length(signal);

  % Frequency vector
  f = linspace(0, fs, E + 1);
  f = f(1:end-1);

  % Mask for frequencies above cutoff frequency + simetry
  mask = ones(E, 1);
  idxs = find(f < cutoff_freq);
  mask(idxs) = 0;
  mask(E - idxs + 1) = 0;

  % Apply the mask to the Fourier Transform
  x_masked = x .* mask;

  % Inverse Fourier Transform to get filtered signal
  signal = ifft(x_masked);
  signal = signal(:);

  % Normalize
  signal = signal / norm(signal, inf);
end
