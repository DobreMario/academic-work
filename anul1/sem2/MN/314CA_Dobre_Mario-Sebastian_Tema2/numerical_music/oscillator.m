function x = oscillator(freq, fs, dur, A, D, S, R)
  x = 0;

  t = 0: 1/fs : dur;
  % Remove the last sample to match duration
  t = t(1 : end - 1);

  % Generate the sinusoidal wave
  wave = sin(2 * pi * freq * t);

  % Save the number of samples
  E = length(t);

  Ea = floor(fs * A);
  Ed = floor(fs * D);
  Er = floor(fs * R);
  % Ensure that the envelope does not exceed the total duration
  Es = E - Ea - Ed - Er;

  % Generate the ADSR envelope
  attack = linspace(0, 1, Ea);
  decay = linspace(1, S, Ed);
  sustain = S * ones(1, Es);
  release = linspace(S, 0, Er);
  envelope = [attack, decay, sustain, release];

  % Amplitude modulation of the signal with the envelope
  x = wave .* envelope;
  x = x(:); 

end
