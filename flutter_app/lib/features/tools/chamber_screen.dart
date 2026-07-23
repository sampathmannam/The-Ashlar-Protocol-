import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/ashlar_colors.dart';
import '../../core/crisis/crisis_detector.dart';
import '../../core/crisis/crisis_support.dart';
import 'chamber_provider.dart';

class ChamberScreen extends ConsumerStatefulWidget {
  const ChamberScreen({super.key});

  @override
  ConsumerState<ChamberScreen> createState() => _ChamberScreenState();
}

class _ChamberScreenState extends ConsumerState<ChamberScreen> {
  final _controller = TextEditingController();
  bool _isReleaseMode = true;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _submit() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;

    // Crisis check
    final crisisResult = CrisisDetector.instance.analyze(text);
    if (crisisResult.isCrisis) {
      CrisisSupportDialog.show(context);
      return;
    }

    ref.read(chamberProvider.notifier).addReflection(
      text,
      keep: !_isReleaseMode,
    );

    _controller.clear();

    // Haptic feedback
    HapticFeedback.mediumImpact();

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          _isReleaseMode
              ? 'Released. The weight lifts.'
              : 'Kept. A stone for your collection.',
        ),
        backgroundColor: AshlarColors.surface,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final chamberState = ref.watch(chamberProvider);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'CHAMBER OF REFLECTION',
            style: Theme.of(context).textTheme.labelLarge?.copyWith(
              letterSpacing: 2,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Write what you carry. Release it or keep it.',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 24),

          // Text input
          TextField(
            controller: _controller,
            maxLines: 5,
            style: TextStyle(color: AshlarColors.lightText),
            decoration: InputDecoration(
              hintText: 'What weighs on you today?',
              hintStyle: TextStyle(color: AshlarColors.mutedText),
              filled: true,
              fillColor: AshlarColors.surface,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(
                  color: AshlarColors.dividerWhite.withAlpha(80),
                ),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(
                  color: AshlarColors.dividerWhite.withAlpha(80),
                ),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(
                  color: AshlarColors.gold,
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Mode toggle
          Row(
            children: [
              Expanded(
                child: GestureDetector(
                  onTap: () => setState(() => _isReleaseMode = true),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    decoration: BoxDecoration(
                      color: _isReleaseMode
                          ? AshlarColors.gold.withAlpha(30)
                          : AshlarColors.surface,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: _isReleaseMode
                            ? AshlarColors.gold.withAlpha(80)
                            : AshlarColors.dividerWhite.withAlpha(60),
                      ),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.delete_outline,
                          size: 18,
                          color: _isReleaseMode
                              ? AshlarColors.gold
                              : AshlarColors.silver,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          'RELEASE',
                          style: Theme.of(context).textTheme.labelSmall?.copyWith(
                            color: _isReleaseMode
                                ? AshlarColors.gold
                                : AshlarColors.silver,
                            letterSpacing: 1,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GestureDetector(
                  onTap: () => setState(() => _isReleaseMode = false),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    decoration: BoxDecoration(
                      color: !_isReleaseMode
                          ? AshlarColors.gold.withAlpha(30)
                          : AshlarColors.surface,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: !_isReleaseMode
                            ? AshlarColors.gold.withAlpha(80)
                            : AshlarColors.dividerWhite.withAlpha(60),
                      ),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.bookmark_outline,
                          size: 18,
                          color: !_isReleaseMode
                              ? AshlarColors.gold
                              : AshlarColors.silver,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          'KEEP',
                          style: Theme.of(context).textTheme.labelSmall?.copyWith(
                            color: !_isReleaseMode
                                ? AshlarColors.gold
                                : AshlarColors.silver,
                            letterSpacing: 1,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          // Submit
          GestureDetector(
            onTap: _submit,
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
                _isReleaseMode ? 'RELEASE THE WEIGHT' : 'KEEP THIS STONE',
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  letterSpacing: 2,
                ),
              ),
            ),
          ),
          const SizedBox(height: 32),

          // Kept reflections
          if (chamberState.keptReflections.isNotEmpty) ...[
            Text(
              'YOUR STONES',
              style: Theme.of(context).textTheme.labelSmall?.copyWith(
                letterSpacing: 1,
              ),
            ),
            const SizedBox(height: 12),
            ...chamberState.keptReflections.map(
              (reflection) => Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AshlarColors.surface,
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(
                      color: AshlarColors.dividerWhite.withAlpha(40),
                    ),
                  ),
                  child: Text(
                    reflection.text,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AshlarColors.lightText,
                    ),
                  ),
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }
}