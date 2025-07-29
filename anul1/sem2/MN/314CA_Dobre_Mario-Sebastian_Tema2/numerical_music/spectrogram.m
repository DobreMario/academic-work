function [S f t] = spectrogram(signal, fs, window_size)
	S = 0;
  f = 0;
  t = 0;

  nr_windows = floor(length(signal) / window_size);
  S = zeros(window_size, nr_windows);
  % Use hanning window for spectral analysis
  hann_window = hanning(window_size);

  for i = 1 : nr_windows
    start_idx = (i - 1) * window_size + 1;
    end_idx = start_idx + window_size - 1;
    % Use the hanning window to taper the signal
    windowed_signal = signal(start_idx:end_idx) .* hann_window;
    
    % Compute the FFT of the windowed signal
    fft_tmp = fft(windowed_signal, 2 * window_size);
    fft_half = abs(fft_tmp(1 : window_size));

    % Store the magnitude of the FFT in the spectrogram matrix
    S(:, i) = fft_half;
  end

  % Frequency vector for the spectrogram
  f = (0:window_size-1)' * fs / (2 * window_size);
  % Time vector for the spectrogram
  t = ((0:nr_windows-1)' * window_size) / fs;

end
