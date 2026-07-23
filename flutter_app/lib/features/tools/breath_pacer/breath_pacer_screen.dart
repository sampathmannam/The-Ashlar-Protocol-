import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/ashlar_colors.dart';
import '../tools_provider.dart';

class BreathPacerScreen extends ConsumerStatefulWidget {
  const BreathPacerScreen({super.key});

  @override
  ConsumerState<BreathPacerScreen> createState() => _BreathPacerScreenState();
}

class _BreathPacerScreenState extends ConsumerState<BreathPacerScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;
  bool _isRunning = false;
  int _breathCount = 0;
  Timer? _timer;

  // 6 breaths/min = 10 seconds per breath
  // 5s in, 5s out
  static const _inhaleSeconds = 5;
  static const _exhaleSeconds = 5;
  static const _totalCycles = 6; // 1 minute

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: _inhaleSeconds),
    );
    _animation = Tween<double>(begin: 0.3, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    _timer?.cancel();
    super.dispose();
  }

  void _startBreathing() {
    setState(() {
      _isRunning = true;
      _breathCount = 0;
    });
    _breathe();
  }

  void _breathe() {
    if (_breathCount >= _totalCycles) {
      _complete();
      return;
    }

    // Inhale
    _controller.duration = const Duration(seconds: _inhaleSeconds);
    _controller.forward(from: 0).then((_) {
      if (!_isRunning) return;

      // Pause between breaths
      Future.delayed(const Duration(milliseconds: 500), () {
        if (!_isRunning) return;

        // Exhale
        _controller.duration = const Duration(seconds: _exhaleSeconds);
        _controller.reverse(from: 1).then((_) {
          if (!_isRunning) return;
          setState(() => _breathCount++);
          _breathe();
        });
      });
    });
  }

  void _complete() {
    setState(() => _isRunning = false);
    ref.read(toolsProvider.notifier).recordWorkDay();
    HapticFeedback.mediumImpact();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AshlarColors.charcoal,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AshlarColors.silver),
          onPressed: () {
            setState(() => _isRunning = false);
            Navigator.pop(context);
          },
        ),
        title: Text(
          'THE LEVEL',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            const SizedBox(height: 48),

            // Breathing visualization
            Expanded(
              child: Center(
                child: AnimatedBuilder(
                  animation: _animation,
                  builder: (context, child) {
                    return Container(
                      width: 200 * _animation.value,
                      height: 200 * _animation.value,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: AshlarColors.gold.withAlpha(
                          (_animation.value * 80).toInt(),
                        ),
                        border: Border.all(
                          color: AshlarColors.gold.withAlpha(
                            (_animation.value * 150).toInt(),
                          ),
                          width: 2,
                        ),
                      ),
                      child: Center(
                        child: Text(
                          _isRunning
                              ? (_animation.value > 0.6 ? 'IN' : 'OUT')
                              : 'BREATHE',
                          style: Theme.of(context).textTheme.labelLarge?.copyWith(
                            color: AshlarColors.gold,
                            letterSpacing: 3,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
            ),

            // Breath count
            if (_isRunning)
              Text(
                '${_breathCount + 1} / $_totalCycles',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  color: AshlarColors.gold,
                ),
              ),
            const SizedBox(height: 24),

            // Instructions
            Text(
              _isRunning
                  ? 'Breathe slowly and deeply.\n6 breaths per minute.'
                  : 'Find a comfortable position.\nPress to begin.',
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AshlarColors.silver,
              ),
            ),
            const SizedBox(height: 32),

            // Start/Stop button
            if (!_isRunning && _breathCount == 0)
              GestureDetector(
                onTap: _startBreathing,
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  decoration: BoxDecoration(
                    color: AshlarColors.gold.withAlpha(30),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: AshlarColors.gold.withAlpha(80),
                    ),
                  ),
                  child: Text(
                    'BEGIN',
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelLarge?.copyWith(
                      letterSpacing: 2,
                    ),
                  ),
                ),
              ),

            if (!_isRunning && _breathCount > 0)
              GestureDetector(
                onTap: () => Navigator.pop(context),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  decoration: BoxDecoration(
                    color: AshlarColors.gold.withAlpha(30),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: AshlarColors.gold.withAlpha(80),
                    ),
                  ),
                  child: Text(
                    'RETURN',
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.labelLarge?.copyWith(
                      letterSpacing: 2,
                    ),
                  ),
                ),
              ),

            const SizedBox(height: 48),
          ],
        ),
      ),
    );
  }
}