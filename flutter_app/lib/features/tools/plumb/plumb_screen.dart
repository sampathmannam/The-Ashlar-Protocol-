import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/theme/ashlar_colors.dart';
import '../../../core/crisis/crisis_detector.dart';
import '../../../core/crisis/crisis_support.dart';
import '../tools_provider.dart';

class PlumbScreen extends ConsumerStatefulWidget {
  const PlumbScreen({super.key});

  @override
  ConsumerState<PlumbScreen> createState() => _PlumbScreenState();
}

class _PlumbScreenState extends ConsumerState<PlumbScreen> {
  final _thoughtController = TextEditingController();
  final _evidenceController = TextEditingController();
  final _reflectionController = TextEditingController();
  int _currentStep = 0;

  @override
  void dispose() {
    _thoughtController.dispose();
    _evidenceController.dispose();
    _reflectionController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < 2) {
      // Crisis check on thought
      if (_currentStep == 0) {
        final result = CrisisDetector.instance.analyze(_thoughtController.text);
        if (result.isCrisis) {
          CrisisSupportDialog.show(context);
          return;
        }
      }

      setState(() => _currentStep++);
    } else {
      _complete();
    }
  }

  void _complete() {
    ref.read(toolsProvider.notifier).recordPlumbSession();
    ref.read(toolsProvider.notifier).recordWorkDay();
    HapticFeedback.mediumImpact();

    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: const Text('Thought straightened. The stone endures.'),
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
    return Scaffold(
      backgroundColor: AshlarColors.charcoal,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AshlarColors.silver),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'THE PLUMB',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Progress
            Row(
              children: List.generate(3, (index) {
                return Expanded(
                  child: Container(
                    height: 3,
                    margin: const EdgeInsets.symmetric(horizontal: 2),
                    decoration: BoxDecoration(
                      color: index <= _currentStep
                          ? AshlarColors.gold
                          : AshlarColors.slate,
                      borderRadius: BorderRadius.circular(1.5),
                    ),
                  ),
                );
              }),
            ),
            const SizedBox(height: 32),

            // Steps
            Expanded(
              child: _currentStep == 0
                  ? _StepOne(controller: _thoughtController)
                  : _currentStep == 1
                      ? _StepTwo(controller: _evidenceController)
                      : _StepThree(controller: _reflectionController),
            ),

            // Next button
            GestureDetector(
              onTap: _nextStep,
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
                  _currentStep < 2 ? 'CONTINUE' : 'STRAIGHTEN',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                    letterSpacing: 2,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StepOne extends StatelessWidget {
  final TextEditingController controller;
  const _StepOne({required this.controller});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'NOTICE THE THOUGHT',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        const SizedBox(height: 12),
        Text(
          'What thought is leaning?',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AshlarColors.silver,
          ),
        ),
        const SizedBox(height: 24),
        TextField(
          controller: controller,
          maxLines: 3,
          style: TextStyle(color: AshlarColors.lightText),
          decoration: InputDecoration(
            hintText: 'I am...',
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
      ],
    );
  }
}

class _StepTwo extends StatelessWidget {
  final TextEditingController controller;
  const _StepTwo({required this.controller});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'EXAMINE THE EVIDENCE',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        const SizedBox(height: 12),
        Text(
          'What facts support this thought?',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AshlarColors.silver,
          ),
        ),
        const SizedBox(height: 24),
        TextField(
          controller: controller,
          maxLines: 3,
          style: TextStyle(color: AshlarColors.lightText),
          decoration: InputDecoration(
            hintText: 'The evidence is...',
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
      ],
    );
  }
}

class _StepThree extends StatelessWidget {
  final TextEditingController controller;
  const _StepThree({required this.controller});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'STRAIGHTEN THE THOUGHT',
          style: Theme.of(context).textTheme.labelLarge?.copyWith(
            letterSpacing: 2,
          ),
        ),
        const SizedBox(height: 12),
        Text(
          'What is a more balanced way to see this?',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AshlarColors.silver,
          ),
        ),
        const SizedBox(height: 24),
        TextField(
          controller: controller,
          maxLines: 3,
          style: TextStyle(color: AshlarColors.lightText),
          decoration: InputDecoration(
            hintText: 'A truer thought is...',
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
      ],
    );
  }
}