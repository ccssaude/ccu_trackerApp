package org.dhis2.usescases.main.program

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import javax.inject.Inject
import org.dhis2.App
import org.dhis2.Bindings.Bindings
import org.dhis2.Bindings.clipWithRoundedCorners
import org.dhis2.Bindings.dp
import org.dhis2.R
import org.dhis2.commons.filters.FilterManager
import org.dhis2.commons.orgunitselector.OUTreeFragment
import org.dhis2.commons.orgunitselector.OnOrgUnitSelectionFinished
import org.dhis2.databinding.FragmentProgramBinding
import org.dhis2.usescases.datasets.datasetDetail.DataSetDetailActivity
import org.dhis2.usescases.general.FragmentGlobalAbstract
import org.dhis2.usescases.main.MainActivity
import org.dhis2.usescases.programEventDetail.ProgramEventDetailActivity
import org.dhis2.usescases.searchTrackEntity.SearchTEActivity
import org.dhis2.utils.Constants
import org.dhis2.utils.HelpManager
import org.dhis2.utils.analytics.SELECT_PROGRAM
import org.dhis2.utils.analytics.TYPE_PROGRAM_SELECTED
import org.dhis2.utils.granularsync.GranularSyncContracts
import org.dhis2.utils.granularsync.SyncStatusDialog
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.ProgramType
import timber.log.Timber

class ProgramFragment : FragmentGlobalAbstract(), ProgramView, OnOrgUnitSelectionFinished {

    private lateinit var binding: FragmentProgramBinding

    @Inject
    lateinit var presenter: ProgramPresenter

    @Inject
    lateinit var adapter: ProgramModelAdapter

    @Inject
    lateinit var animation: ProgramAnimation

    // -------------------------------------------
    //region LIFECYCLE

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            (it.applicationContext as App).userComponent()?.plus(ProgramModule(this))?.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program, container, false)
        ViewCompat.setTransitionName(binding.drawerLayout, "contenttest")
        binding.lifecycleOwner = this
        (binding.drawerLayout.background as GradientDrawable).cornerRadius = 0f
        return binding.apply {
            presenter = this@ProgramFragment.presenter
            drawerLayout.clipWithRoundedCorners(16.dp)
            programRecycler.itemAnimator = null
            programRecycler.adapter = adapter
            programRecycler.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }.root
    }

    override fun onResume() {
        super.onResume()
        presenter.init()
        animation.initBackdropCorners(
            binding.drawerLayout.background.mutate() as GradientDrawable
        )
    }

    override fun onPause() {
        animation.reverseBackdropCorners(
            binding.drawerLayout.background.mutate() as GradientDrawable
        )
        presenter.dispose()
        super.onPause()
    }

    //endregion

    override fun swapProgramModelData(programs: List<ProgramViewModel>) {
        binding.progressLayout.visibility = View.GONE
        binding.emptyView.visibility = if (programs.isEmpty()) View.VISIBLE else View.GONE
        (binding.programRecycler.adapter as ProgramModelAdapter).setData(programs)
    }

    override fun showFilterProgress() {
        binding.progressLayout.visibility = View.VISIBLE
        Bindings.setViewVisibility(
            binding.clearFilter,
            FilterManager.getInstance().totalFilters > 0
        )
    }

    override fun renderError(message: String) {
        if (isAdded && activity != null) {
            AlertDialog.Builder(requireActivity())
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .show()
        }
    }

    override fun openOrgUnitTreeSelector() {
        OUTreeFragment.newInstance(
            true,
            FilterManager.getInstance().orgUnitFilters.map { it.uid() }.toMutableList()
        ).apply {
            selectionCallback = this@ProgramFragment
        }.show(childFragmentManager, "OUTreeFragment")
    }

    override fun onSelectionFinished(selectedOrgUnits: List<OrganisationUnit>) {
        presenter.setOrgUnitFilters(selectedOrgUnits)
    }

    override fun setTutorial() {
        try {
            if (context != null && isAdded) {
                Handler().postDelayed(
                    {
                        if (abstractActivity != null) {
                            val stepCondition = SparseBooleanArray()
                            stepCondition.put(
                                7,
                                binding.programRecycler.adapter!!.itemCount > 0
                            )
                            HelpManager.getInstance().show(
                                abstractActivity,
                                HelpManager.TutorialName.PROGRAM_FRAGMENT,
                                stepCondition
                            )
                        }
                    },
                    500
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun openFilter(open: Boolean) {
        binding.filter.visibility = if (open) View.VISIBLE else View.GONE
    }

    override fun showHideFilter() {
        (activity as MainActivity).showHideFilter()
    }

    override fun clearFilters() {
        (activity as MainActivity).newAdapter.notifyDataSetChanged()
    }

    override fun navigateTo(program: ProgramViewModel) {
        val bundle = Bundle()
        val idTag = if (program.programType().isEmpty()) {
            Constants.DATASET_UID
        } else {
            Constants.PROGRAM_UID
        }

        if (!TextUtils.isEmpty(program.type())) {
            bundle.putString(Constants.TRACKED_ENTITY_UID, program.type())
        }

        abstractActivity.analyticsHelper.setEvent(
            TYPE_PROGRAM_SELECTED,
            if (program.programType().isNotEmpty()) {
                program.programType()
            } else {
                program.typeName()
            },
            SELECT_PROGRAM
        )
        bundle.putString(idTag, program.id())
        bundle.putString(Constants.DATA_SET_NAME, program.title())
        bundle.putString(
            Constants.ACCESS_DATA,
            program.accessDataWrite().toString()
        )

        when (program.programType()) {
            ProgramType.WITH_REGISTRATION.name ->
                startActivity(SearchTEActivity::class.java, bundle, false, false, null)
            ProgramType.WITHOUT_REGISTRATION.name ->
                startActivity(
                    ProgramEventDetailActivity::class.java,
                    ProgramEventDetailActivity.getBundle(program.id()),
                    false, false, null
                )
            else -> startActivity(DataSetDetailActivity::class.java, bundle, false, false, null)
        }
    }

    override fun showSyncDialog(program: ProgramViewModel) {
        val dialog = SyncStatusDialog.Builder()
            .setConflictType(
                if (program.programType().isNotEmpty()) {
                    SyncStatusDialog.ConflictType.PROGRAM
                } else {
                    SyncStatusDialog.ConflictType.DATA_SET
                }
            )
            .setUid(program.id())
            .onDismissListener(
                object : GranularSyncContracts.OnDismissListener {
                    override fun onDismiss(hasChanged: Boolean) {
                        if (hasChanged) {
                            presenter.updateProgramQueries()
                        }
                    }
                })
            .build()

        dialog.show(abstractActivity.supportFragmentManager, FRAGMENT_TAG)
    }

    fun sharedView() = binding.drawerLayout

    companion object {
        const val FRAGMENT_TAG = "SYNC"
    }
}
